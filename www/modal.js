var exec = require('cordova/exec');

var modal = {
  /**
   * Open a fullscreen modal
   * @param {Function} successCallback
   * @param {Function} errorCallback
   * @param {String} url
   * @param {Number} dismissMode - 0=undismissable, 1=X only, 2=X+swipe (default=2)
   * @param {Number} closeButtonPosition - 0=hidden (default), 1=left, 2=right
   */
  open: function(successCallback, errorCallback, url, dismissMode, closeButtonPosition) {
    exec(successCallback, errorCallback, 'Modal', 'open', [
      url,
      dismissMode || 2,
      closeButtonPosition || 0
    ]);
  },

  /**
   * Open a half-screen (sheet) modal
   */
  openHalf: function(successCallback, errorCallback, url, dismissMode, closeButtonPosition) {
    exec(successCallback, errorCallback, 'Modal', 'openHalf', [
      url,
      dismissMode || 2,
      closeButtonPosition || 0
    ]);
  },

  /**
   * Close the modal (from inside JS)
   */
  close: function(data) {
    exec(null, null, 'Modal', 'close', [data]);
  }
};

module.exports = modal;