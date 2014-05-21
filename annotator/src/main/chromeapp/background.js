chrome.app.runtime.onLaunched.addListener(function() {
    chrome.app.window.create('annotator.html', {
        'bounds': {
            'width': 400,
            'height': 500
        }
    });
});