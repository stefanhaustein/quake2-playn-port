var time = new Date().getTime();

window.requestFileSystem  = window.requestFileSystem || window.webkitRequestFileSystem;

println("");
zip.useWebWorkers = false;

var FS_SIZE = 200 * 1024 * 1024;

if (!window.requestFileSystem) {
   println("File System not available; try this demo with Google Chrome or a different browser with full HTML5 support.");
} else {
   println("Initialing file system. Requesting user permission for 200M persistent memory.");
   window.requestFileSystem(PERSISTENT, FS_SIZE, onInitFs,
     function(msg) {
       println("Persistent memory denied. Using temporary memory.");
       window.requestFileSystem(TEMPORARY, 100*1024*1024, onInitFs, 
         function(msg) {
           error("Error requesting temporary storage: " + msg);
         }
       );
     });
}


function println(s) {
  document.getElementById("log").textContent += s +"\n";
  document.getElementById("log-bottom").scrollIntoView();
}

function backspace(cnt, s) {
  text = document.getElementById("log").textContent;
  document.getElementById("log").textContent = text.substring(0, text.length - cnt) + s +"\n";
}

function error(msg) {
  println("ERROR: " + msg);
}

function fsErrorHandler(msg) {
  println("ERROR: " + msg);
}

function done() {
  // Fix active waiting!
  window.quakeFileSystemReady = true;
}

function onInitFs(fileSystem) {
  println("File system initialized. Checking contents.")
  window.quakeFileSystem = fileSystem;

  window.quakeFileSystem.root.getFile("splash/wav/btnx.wav", {},
    function() {
      println("Files downloaded and unpacked already.");
      done();
    },
    function() {
      println("Files not available. Waiting for user to provide URL and press 'Start'.");
      println("");
    }
  );
}

function downloadAndUnpack() {
  if (!window.requestFileSystem) {
     error("File System not available; try this demo with Google Chrome or a different browser with full HTML5 support.");
     return;
  }

  var url = document.getElementById('source_url').value;
  println("Donwloading and inflating " + url);
  zip.createReader(new zip.HttpReader(url), function(reader) { 
    println("Created ZIP reader, getting entries");
    reader.getEntries(function(entries) {
      processZipEntries(entries, 0);
    }); // getEntries
  }, function(msg) {
    error("Creating a ZIP reader failed: " + msg);
  });
}


function processZipEntries(entries, startIndex) {
  if (startIndex >= entries.length) {
    println("Decompression done.")
    done();
    return;
  }
  var entry = entries[startIndex];
  var fileName = entry.filename;
  if (entry.directory) {
    println("Processing directory " + fileName);
    processZipEntries(entries, startIndex + 1)
  }
  println("Unpacking: " + startIndex + "/" + entries.length + ": " + fileName + " ...    ");

  createQuakeFile(fileName, function(fileEntry) {
    entry.getData(new zip.FileWriter(fileEntry), function() {
      backspace(4, "Done");
      processZipEntries(entries, startIndex + 1)
    }, function(current, total) {
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
    root.getDirectory(parts[index], {create: true}, function(dirEntry) {
      createFileImpl(dirEntry, parts, index + 1, callback);
    });
  }
}


