var FS_SIZE = 200 * 1024 * 1024;
var time = new Date().getTime();


// =================================================================== utilities


function println(s) {
  document.getElementById("log").textContent += s +"\n";
  document.getElementById("log-bottom").scrollIntoView();
}

function backspace(cnt, s) {
  text = document.getElementById("log").textContent;
  document.getElementById("log").textContent = 
    text.substring(0, text.length - cnt) + s +"\n";
}

function error(msg) {
  println("ERROR: " + msg);
}

function fsErrorHandler(msg) {
  println("ERROR: " + msg);
}


// =================================== download callbacks in order of processing


function downloadAndUnpack() {
  if (!window.requestFileSystem) {
     error("File System not available; try this demo with Google Chrome or a different browser with full HTML5 support.");
     return;
  }

  var url = document.getElementById('source_url').value;
  // println("Donwloading and inflating " + url);
  println("Donwloading and inflating");
  zip.createReader(new zip.HttpReader(url), function(reader) { 
    println("Created ZIP reader, getting entries");
    reader.getEntries(function(zipEntries) {
      processZipEntries(zipEntries, 0);
    }); // getEntries
  }, function(msg) {
    error("Creating a ZIP reader failed: " + msg);
  });
}

function processZipEntries(zipEntries, startIndex) {
  if (startIndex >= zipEntries.length) {
    println("Decompression done.")
    done();
    return;
  }
  var zipEntry = zipEntries[startIndex];
  var fileName = zipEntry.filename;
  if (zipEntry.directory) {
    println("Processing directory " + fileName);
    processZipEntries(zipEntries, startIndex + 1)
  }
  println("Unpacking: " + (startIndex + 1) + "/" + zipEntries.length + ": " + fileName + " ...    ");

  createQuakeFile(fileName, function(fileEntry) {
    // blobWriter = new zip.BlobWriter("application/binary");
    // console.log("created blobwriter", blobWriter);
    zipEntry.getData(new zip.FileWriter(fileEntry), function() {
      /*fileEntry.createWriter(function(writer) {
        blobWriter.getData(function(blob) {
          writer.write(blob);
          console.log("done: ", blobWriter);*/
          backspace(4, "Done");
          processZipEntries(zipEntries, startIndex + 1);
        
    //  }, error);
    }, function(current, total) {
      console.log("update", current, total);
      var newTime = new Date().getTime();
      if (newTime - time > 4000) {
        time = newTime;
        
        var percent = current/total*99;
        var s = String.fromCharCode(48 + percent / 10) + String.fromCharCode(48 + percent % 10) + "%";
        backspace(4, s);
      }
    })
  });
}

function createQuakeFile(fileName, callback) {
  var parts = fileName.toLowerCase().split("/");
  createFileImpl(quakeFileSystem.root, parts, 0, callback);
}

function createFileImpl(root, parts, index, callback) {
  if (index == parts.length - 1) {
    root.getFile(parts[index], {create: true}, callback);
  } else {
    root.getDirectory(parts[index], {create: true}, 
      function(dirEntry) {
        createFileImpl(dirEntry, parts, index + 1, callback);
      },
      function(e) {
        error("error obtaining directory " + parts[index] + ": " +e);
        window.console.log(e);
      });
  }
}

function done() {
  // Fix active waiting!
  window.quakeFileSystemReady = true;
}


// ======================================= main callbacks in order of processing


function requestPersistentFs() {
  window.requestFileSystem(window.PERSISTENT, FS_SIZE, onInitFs, requestTempFs); 
}

function requestTempFs(msg) {
  if (msg) {
    error(msg);
  }
  println("Persistent memory N/A. Using temporary memory.");
  window.requestFileSystem(window.TEMPORARY, 100*1024*1024, onInitFs, 
  function(msg) {
    error(msg);
    println("Giving up.")
  });
}

function onInitFs(fileSystem) {
  println("File system initialized. Checking contents.")
  window.quakeFileSystem = fileSystem;

  window.quakeFileSystem.root.getFile("splash/wav/btnx.wav", {},
    function() {
      println("Files downloaded and unpacked already.");
      done();    
      // document.getElementById("download_dialog").style.display = "block";
    },
    function() {
      println("Files not available. Waiting for user to provide URL and press 'Start'.");
      println("");
      document.getElementById("download_dialog").style.display = "block";
    }
  );
}


// ======================================================================= main


println("");
zip.useWebWorkers = false;
//zip.workerScriptsPath = "lib/";

window.requestFileSystem  = window.requestFileSystem || 
    window.webkitRequestFileSystem;

// If we can ask for persistent storage, do so.
if (window.webkitStorageInfo) {
  println("Quota API available. Asking for persistent storage.");
  println("If a browser dialog appears at the top of the screen, please confirm.");
  window.webkitStorageInfo.requestQuota(
      PERSISTENT, FS_SIZE, requestPersistentFs, requestTempFs);
} else {
  requestPersistentFs(FS_SIZE);
}