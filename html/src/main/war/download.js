function println(s) {
  document.getElementById("log").innerText += s +"\n";
  document.getElementById("log-bottom").scrollIntoView();
}

function backspace(cnt, s) {
  text = document.getElementById("log").innerText;
  document.getElementById("log").innerText = text.substring(0, text.length - cnt) + s +"\n";
}

function error(msg) {
  println("ERROR: " + msg);
}

function fsErrorHandler(msg) {
  println("ERROR: " + msg);
}

window.requestFileSystem  = window.requestFileSystem || window.webkitRequestFileSystem;

println("");

window.storageInfo = window.storageInfo || window.mozStorageInfo || window.webkitStorageInfo;

if (!window.storageInfo) {
   println("File System not available.");
   println("Try this demo with Google Chrome or a different browser with full HTML5 support.")
} else {
   println("Initialing file system. Requesting user permission for 200M persistent memory.");
}

window.storageInfo.requestQuota(PERSISTENT, 200*1024*1024, 
  function(grantedBytes) {
    window.requestFileSystem(PERSISTENT, grantedBytes, onInitFs, 
      function(msg) {
        error("Error requesting persistent storage: " + msg);
      }
    );
  }, 
  function() {
    println("Persistent memory denied. Using temporary memory.");
    window.requestFileSystem(TEMPORARY, 100*1024*1024, onInitFs, 
      function(msg) {
        error("Error requesting temporary storage: " + msg);
      }
    );
  }
);

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
  var url = document.getElementById('source_url').value;
  println("Donwloading and inflating " + url);
  zip.createReader(new zip.HttpReader(url), function(reader) {
  reader.getEntries(function(entries) {
    processZipEntries(entries, 0);
    }); // getEntries
  }, function(msg) {
    error("Creating a ZIP reader failed: " + msg);
  });
}


var time = new Date().getTime();

function processZipEntries(entries, startIndex) {
  if (startIndex >= entries.length) {
    println("Decompression done.")
    done();
    return;
  }
  var entry = entries[startIndex];
  if (entry.directory) {
    processZipEntries(entries, startIndex + 1)
  }
  var fileName = entry.filename;
  println("Unpacking: " + fileName + " ...    ");

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
  var parts = fileName.split("/");
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



    // // get first entry content as blob
    // entries[0].getData(new zip.BlobWriter(), function(blob) {
    // console.log(text);
    // // close the zip reader
    //  reader.close(function() {
    //          // onclose callback
    //       });

    //      }, function(current, total) {
    //        // onprogress callback
    //      });


/*
println("Starting Downloader.");

var req = new XMLHttpRequest();
 
req.addEventListener("progress", updateProgress, false);
req.addEventListener("load", transferComplete, false);
req.addEventListener("error", transferFailed, false);
req.addEventListener("abort", transferCanceled, false);
 
println("Listeners registered");

req.open("GET", "id/q2-314-demo-x86.exe", true);
req.responseType = "arraybuffer";
req.send();

println("Request opened. Type = 'arraybuffer'");

function updateProgress(evt) {
  if (evt.lengthComputable) {
    var percentComplete = evt.loaded / evt.total;
    println("Percent: " + percentComplete);
  } else {
    println("Unknown Progress...")
  }
}
 
function transferComplete(evt) {
  println("The transfer is complete.");
}

function transferFailed(evt) {
  println("An error occurred while transferring the file.");
}
	 
function transferCanceled(evt) {
  alert("The transfer has been canceled by the user.");
}
*/