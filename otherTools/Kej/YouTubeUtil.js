/*global console, unescape, md5, util */

// See [YouTube Data API (v3)]
// https://developers.google.com/youtube/v3/
// See [YouTube Data API - Guides and Tutorials]
// https://developers.google.com/youtube/v3/guides/
// See [Getting Started with the YouTube Data API]
// https://developers.google.com/youtube/v3/getting-started
// See [YouTube Data API - Errors]
// https://developers.google.com/youtube/v3/docs/errors
// Migrating Your Application to YouTube Data API (v3)
// https://developers.google.com/youtube/v3/migration-guide
// Revision History
// https://developers.google.com/youtube/v3/revision_history

var ytobj = (function () {
    'use strict';

    // API Keys, 
    // See [Obtaining authorization credentials] 
    // https://developers.google.com/youtube/registering_an_application
    // and [Google Developer Console > API Project > APIs & auth > APIs] 
    // https://console.developers.google.com/project
    var clientId = '';
    var clientSecret = '';
    var appKey = '';

    // See [Implementing OAuth 2.0 Authentication] 
    // https://developers.google.com/youtube/v3/guides/authentication#installed-apps
    var gglOAuth2_0 = {
        "ServerUrl": "https://accounts.google.com/o/oauth2/auth?",
        "TokenUrl": "https://accounts.google.com/o/oauth2/token",
        "RedirectUri": "urn:ietf:wg:oauth:2.0:oob",
        "Scope": {
            "Manage": "https://www.googleapis.com/auth/youtube",
            "View": "https://www.googleapis.com/auth/youtube.readonly",
            "Upload": "https://www.googleapis.com/auth/youtube.upload"
        },
        "Approval": "https://accounts.google.com/o/oauth2/approval?"
    };

    // API's Abbrivation
    var AuthWeb = Windows.Security.Authentication.Web;

    // Used for checking login status
    // Returned tokens after OAuth2.0 authentication
    var appLocalSettings = Windows.Storage.ApplicationData.current.localSettings;
    var refreshToken = appLocalSettings.values['youtubeRefreshToken'];
    var returnedToken = null;
    var accessToken = appLocalSettings.values['youtubeAccessToken'];
    var tokenType = null;
    var authenticatePromise = null;

    // See [YouTube Data API - Guides and Tutorials]
    // https://developers.google.com/youtube/v3/guides/
    // and [API Reference]
    // https://developers.google.com/youtube/v3/docs/
    var ytDataAPI3 = "https://www.googleapis.com/youtube/v3/";

    // See [YouTube Data API - Resumable Uploads]
    // https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol
    var ytUploadAPI3 = "https://www.googleapis.com/upload/youtube/v3/"

    // Use along with ytDataAPI3, See [API Reference > Resource types]
    // https://developers.google.com/youtube/v3/docs/
    var ytAPI = {
        "channels": "channels?",
        "playlistItems": "playlistItems?",
        "playlists": "playlists?",
        "search": "search?",
        "subscriptions": "subscriptions?",
        "videos": "videos?",
        "videoCategories": "videoCategories?"
    };

    // See [Getting Started with the YouTube Data API > Understanding the part parameter]
    // https://developers.google.com/youtube/v3/getting-started#part
    var ytPART = {
        "id": "id",
        "snippet": "snippet",
        "contentDetails": "contentDetails",
        "statistics": "statistics",
        "fileDetails": "fileDetails",
        "player": "player",
        "processingDetails": "processingDetails",
        "recordingDetails": "recordingDetails",
        "status": "status",
        "suggestions": "suggestions",
        "topicDetails": "topicDetails"
    };

    // Constant field for fetching Featured (Most Popular videos)
    // See [Videos: list] > [Parameters] > [Filters] > [chart]
    // https://developers.google.com/youtube/v3/docs/videos/list#chart
    var ytCHART = { "mostPopular": "mostPopular" };

    // Constants for YouTube API v3.0 order parameter
    // See [Search: list] > [Parameters] > [Optional parameters] > [order]
    // https://developers.google.com/youtube/v3/docs/search/list#order
    var ytORDER = {
        "date": "date",
        "rating": "rating",
        "relevance": "relevance",
        "title": "title",
        "videoCount": "videoCount",
        "viewCount": "viewCount"
    };

    // Constants for YouTube API v3.0 type parameter
    // See [Search: list] > [Parameters] > [Optional parameters] > [type]
    // https://developers.google.com/youtube/v3/docs/search/list#type
    var ytTYPE = {
        "video": "video",
        "channel": "channel",
        "playlist": "playlist"
    };

    // Constants for YouTube API v3.0 type parameter, Respective to ytTYPE
    // The type of the API resource.
    // See [Search] > [Resource representation] > [Properties] > [id]
    // https://developers.google.com/youtube/v3/docs/search#id.kind
    var ytID_KIND = { // result of search with given ytTYPE
        "video": "youtube#video",
        "playlist": "youtube#playlist",
        "channel": "youtube#channel"
    }

    // Constants for YouTube API v3.0 relatedPlaylists
    // See [Channels] > [Properties] > [contentDetails]
    // https://developers.google.com/youtube/v3/docs/channels#contentDetails.relatedPlaylists
    var ytRELATED_PLAYLISTS = {
        "likes": "likes",
        "favorites": "favorites",
        "uploads": "uploads",
        "watchHistory": "watchHistory",
        "watchLater": "watchLater"
    };

    // Constants for YouTube API v3.0 video status, 
    // Contains uploadStatus, failureReason, rejectionReason, privacyStatus, etc.
    // See [Videos] > [Properties] > [status]
    // https://developers.google.com/youtube/v3/docs/videos#status
    var ytVideoStatus = {
        "uploadStatus": {
            "deleted" : "deleted",
            "failed": "failed",
            "processed": "processed",
            "rejected": "rejected",
            "uploaded": "uploaded",
        },
        // On the https://www.youtube.com , privacy has three type and displayed in icon
        "privacyStatus": {
            "private": "private", // Locked icon
            "public": "public", // No icon
            "unlisted": "unlisted", // Unlicked icon
        }
    }

    // App accepts these query type from ytService
    var queryType = {
        "MOST_VIEWED": "MOST_VIEWED",
        "MOST_POPULAR": "MOST_POPULAR",
        "FAVORITE": "FAVORITE",
        "PLAYLIST": "PLAYLIST",
        "SUBSCRIPTION": "SUBSCRIPTION",
        "MOVIE_TRAILER": "MOVIE_TRAILER",
        "PLAYLIST_SET": "PLAYLIST_SET",
        "SUBSCRIPTION_SET": "SUBSCRIPTION_SET",
        "SEARCH": "SEARCH",
        "MY_UPLOADED_VIDEO": "MY_UPLOADED_VIDEO"
    };

    // Stored for fetching next page
    var pageTokens = {
        "next": null,
        "prev": null
    };

    // kejVideoFetcherUrl and ytWatchUrl are used for fetching watchable urls
    var kejVideoFetcherUrl = "http://www.youtube.com/get_video_info?";
    var ytWatchUrl = "http://www.youtube.com/watch?";

    // kej's important parameters history, order as [newest, ..., oldest]
    // http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20150806_1420
    var kejParamHistory = [
        // Valid >> Valid from 2015/08/06 // Expires End ??
        // v=20150806_1420
        {"sts" : 16651, "sCode" : [0, 32, -3, 0, -1, 0]},
        // Deprecated >> Valid from 2015/06/24 // Expires End 2015/08/05
        // v=20150622_2338
        {"sts" : 16603, "sCode" : [20, -2, 11, -3, 0, -1, 2, 15]},
        // Deprecated >> Valid from 2015/06  // Expires End 2015/06/24
        // v=20150604_0050
        {"sts" : 16582, "sCode" : [0, 41, -3, 69, -1, 66, 0, 27, -2]},
        // Deprecated >> Valid from 2015/05  // Expires End 2015/06
        // v=20150505_0007
        {"sts" : 16554, "sCode" : [0, -1, 40, -2, 0, 6, -3, 60]},
        // Deprecated >> Valid from 2013/11  // Expires End 2015/05
        // sCode should be equivalent to SigHandlerAlternative_20131107
        {"sts" : 1586, "sCode" : [52, 62, 83, 62, -3, 0, -3]},
    ];

    // Youtube clip formats
    // Also see source code of http://kej.tw/flvretriever/
    // After apply link of PSY - GANGNAM STYLE (강남스타일) M/V
    // http://kej.tw/flvretriever/youtube.php?videoUrl=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3D9bZkp7q19f0
    // --- Deprecated ---
    // >> Valid from 2013/11  // Expires End 2015/05
    // reveals : <script type="text/javascript" src="script/parse.youtube.fmt_url_map.js?v=20131107"></script>
    // thus content refers to http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20131107
    // --- Valid ---
    // >> Valid from 2015/05  // Expires End ??
    // reveals : <script type="text/javascript" src="script/parse.youtube.fmt_url_map.js?v=20150505_0007"></script>
    // thus content refers to http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20150505_0007
    var fmtStr = [];
    fmtStr[18] = '(MP4(H.264), 640 x 360, Stereo 44KHz AAC)';
    fmtStr[22] = '(MP4(H.264), 1280 x 720, Stereo 44KHz AAC)';
    fmtStr[37] = '(MP4(H.264), 1920 x 1080, Stereo 44KHz AAC)';
    fmtStr[38] = '(MP4(H.264), 4096 x 3072, Stereo 44KHz AAC)';
    fmtStr[83] = '(MP4(H.264), 854 x 240, Stereo 44KHz AAC)';
    fmtStr[82] = '(MP4(H.264), 640 x 360, Stereo 44KHz AAC)';
    fmtStr[85] = '(MP4(H.264), 1920 x 520, Stereo 44KHz AAC)';
    fmtStr[84] = '(MP4(H.264), 1280 x 720, Stereo 44KHz AAC)';

    function log(message) {
        console.log('[YouTubeUtil.js] ' + message);
    }

    function getErrorCode(error) {
        var errorCode = util.ERROR_TYPE.UNDEFINED;

        // API Abbreviation
        var WebError = Windows.Web.WebErrorStatus;
        if (error.readyState === error.DONE) {
            switch (error.status) {
                case WebError.unknown: // case 0:
                    errorCode = util.ERROR_TYPE.NETWORK_UNAVAILABLE;
                    break;
                case WebError.unauthorized: // case 401:
                    if (error.statusText === "NoLinkedYouTubeAccount") {
                        errorCode = util.ERROR_TYPE.NoLinkedYouTubeAccount;
                    }
                    else if (error.statusText === "Token invalid - Invalid token: Stateless token expired") {
                        errorCode = util.ERROR_TYPE.ACCESS_TOKEN_EXPIRED;
                    }
                    else {
                        errorCode = util.ERROR_TYPE.HTTP_STATUS_401;
                    }
                    break;
                case WebError.badRequest: //case 400:
                    errorCode = util.ERROR_TYPE.HTTP_STATUS_400;
                    break;
                case WebError.forbidden: //case 403:
                    if (error.responseText.match("User account suspended")) {
                        errorCode = util.ERROR_TYPE.USER_ACCOUT_SUSPENDED;
                    }
                    else {
                        errorCode = util.ERROR_TYPE.HTTP_STATUS_403;
                    }
                    break;
                case WebError.notFound: // case 404:
                    errorCode = util.ERROR_TYPE.HTTP_STATUS_404;
                    break;
                case WebError.internalServerError: // case 500:
                    errorCode = util.ERROR_TYPE.HTTP_STATUS_500;
                    break;
                case WebError.notImplemented: // case 501:
                    errorCode = util.ERROR_TYPE.HTTP_STATUS_501;
                    break;
                case WebError.serviceUnavailable: // case 503:
                    errorCode = util.ERROR_TYPE.HTTP_STATUS_503;
                    break;
            }
        }
        return errorCode;
    }

    function errorCallback(msg, errorCallback, errorCode) {
        log(msg);
        util.callback(errorCallback, errorCode);
    }

    // convert objects key-value into <key>=<value>(&<key>=<value>)*
    // E.g. paramAsUrlString({part: "snippet", q:"query", pageToken: undefined}) returns "part=snippet&q=query"
    function paramAsUrlString(paramSet) {
        var paramString = "";

        for (var key in paramSet) {
            if (paramSet[key]) {
                if (paramString.length > 0) {
                    paramString += "&";
                }
                paramString += "" + key + "=" + paramSet[key];
            }
        }
        return paramString;
    }

    // Returns the parameter (of prefix) peeked in response
    // E.g. Returns "Hello" if peekParams("a=1&b=2&c=Hello&d=3", "c=", "&")
    function peekParams(response, prefix, ending) {
        var beginAt = response.indexOf(prefix);
        if (beginAt < 0) {
            Debug.writeln("Fail to find prefix " + prefix);
            return response;
        }
        var endAt = response.indexOf(ending, beginAt);
        if (endAt === -1) { // Set the end as length if <prefix> is the last parameter
            endAt = response.length;
        }

        // See [JavaScript decodeURIComponent() Function]
        // http://www.w3schools.com/jsref/jsref_decodeuricomponent.asp
        // fmtContent is the full content of fmtKey
        return response.substring(beginAt + prefix.length, endAt);
    }

    // Returns the object with access token, used for headers for WinJS.xhr
    function getAccessTokenHeader() {
        return {'Authorization': tokenType + ' "' + accessToken + '"'};
    }

    // Clear the stored login access tokens and user infos
    function logout() {
        appLocalSettings.values['youtubeAccessToken'] = "";
        accessToken = null;
        appLocalSettings.values['youtubeRefreshToken'] = "";
        refreshToken = null;
        returnedToken = null;
        tokenType = null;
        appLocalSettings.values['youtubeUserInfo'] = JSON.stringify({});
    }

    // See parameters of [WinJS.xhr function]
    // http://msdn.microsoft.com/en-us/library/windows/apps/br229787.aspx
    function sendYoutubeHttpRequestAsync(requestUrl, requestHeader) {
        return new WinJS.Promise(function (complete, error) {
            WinJS.xhr({ type: "GET", url: requestUrl, headers: requestHeader }).then( function (response) {
                try {
                    response = JSON.parse(response.responseText);
                } catch (errorMsg) {
                    errorCallback("sendYoutubeHttpRequestAsync() Parse error: " + errorMsg, error, util.ERROR_TYPE.JSON_PARSE_ERROR);
                    return;
                }
                complete(response);
            }, function (errorMsg) {
                var errorCode = getErrorCode(errorMsg);
                error(errorCode);
            });
        });
    }

    // Get the YouTube user's information by sending channel query
    // For current user => option = {mine:true}
    // For general user => option = {channelId: <channelId>}
    // Achieve the query by getChannelInfoAsync()
    function getUserInfoAsync(option) {
        return new WinJS.Promise(function (complete, error) {
            if (!option) {
                errorCallback("getUserInfoAsync() Wrong parameters!! " + option, error, util.ERROR_TYPE.WRONG_PARAM);
                return;
            }

            if (option.mine) {
                if (accessToken) {
                    getChannelInfoAsync(option).then(complete, error);
                } else {
                    errorCallback("getUserInfoAsync() Not login!", error, util.ERROR_TYPE.NOT_LOGIN);
                }
            } else if (option.channelId) {
                getChannelInfoAsync(option).then(complete, error);
            } else {
                errorCallback("getUserInfoAsync() Wrong parameters!! " + option, error, util.ERROR_TYPE.WRONG_PARAM);
            }
        });
    }

    // If success on OAuth2.0 request, handle the response of  XMLHttpRequest
    // Login (oauth 2.0)
    // See https://developers.google.com/youtube/v3/guides/authentication
    // Step 1: Obtain an access token
    function onWebAuthSuccessAsync(result) {
        return new WinJS.Promise(function (complete, error) {
            log("onWebAuthSuccessAsync() Status returned by WebAuth broker: " + result.responseStatus);

            var status = result.responseStatus;
            var data = result.responseData;
            var errorDetail = result.responseErrorDetail;
            // [0, 1, 2] = success, userCancel, errorHttp
            if (status === AuthWeb.WebAuthenticationStatus.userCancel) {
                errorCallback("onWebAuthSuccessAsync() Response status: User canceled! Detail: " + errorDetail, error, util.ERROR_TYPE.USER_CANCEL);
                return;
            } else if (status === AuthWeb.WebAuthenticationStatus.errorHttp) {
                errorCallback("onWebAuthSuccessAsync() Response status: HTTP error! Detail: " + errorDetail, error, util.ERROR_TYPE.HTTP_ERROR);
                return;
            } else { } // status === AuthWeb.WebAuthenticationStatus.success = 0

            if (data === '') {
                errorCallback("onWebAuthSuccessAsync() [ERROR] No responseData!", error, util.ERROR_TYPE.NO_RESPONSE_DATA);
                return;
            } else if (data.indexOf("access_denied") >= 0) {
                errorCallback("onWebAuthSuccessAsync() [ERROR] No responseData!", error, util.ERROR_TYPE.ACCESS_DENIED);
                return;
            }

            returnedToken = result.responseData.split("=")[1];
            getAccessWebAuthAsync().then(complete, error);
        });
    }

    // Implementing OAuth 2.0 Authentication
    // https://developers.google.com/youtube/v3/guides/authentication
    // Step 2+3: Handle response from Google
    // Step 4: Exchange authorization code for refresh and access tokens
    function getAccessWebAuthAsync() {
        return new WinJS.Promise(function (complete, error) {
            if (returnedToken === null && refreshToken === null) {
                errorCallback("getAccessWebAuthAsync() token value error", error, util.ERROR_TYPE.NO_OAUTH_TOKEN);
                return;
            }

            var reqHeaders = { "Content-Type": "application/x-www-form-urlencoded" };

            // Exchange authorization code for refresh and access tokens
            var dataExchangeSet = {};
            if (refreshToken) {
                dataExchangeSet = {
                    "client_id": clientId,
                    "client_secret": clientSecret,
                    "refresh_token": refreshToken,
                    "grant_type": "refresh_token"
                };
            } else if (returnedToken) {
                dataExchangeSet = {
                    "code": returnedToken,
                    "client_id": clientId,
                    "client_secret": clientSecret,
                    "redirect_uri": gglOAuth2_0.RedirectUri,
                    "grant_type": "authorization_code"
                };
            } else { }

            var xhrOptions = {
                type: "POST",
                url: gglOAuth2_0.TokenUrl,
                data: paramAsUrlString(dataExchangeSet),
                headers: reqHeaders
            };

            WinJS.xhr(xhrOptions).then( function (response) {
                parseAccessToken(response, complete, error);
            }, function (err) {
                log("getAccessWebAuthAsync(). Error Number: " + err.number + " Error Message: " + err.message);
                var errorCode = getErrorCode(err);
                error(errorCode);
            });
        });
    }

    // Retrieve accessToken and refreshToken from result
    // https://developers.google.com/youtube/v3/guides/authentication
    // Step 5: Process response and store tokens
    function parseAccessToken(result, complete, error) {
        var jsonObj = null;
        try {
            jsonObj = JSON.parse(result.responseText);
        } catch (errorMsg) {
            errorCallback("parseAccessToken() Parse error: " + errorMsg, error, util.ERROR_TYPE.JSON_PARSE_ERROR);
            return;
        }

        // Store accessToken if exists
        if (jsonObj.access_token) {
            accessToken = jsonObj.access_token;
            tokenType = jsonObj.token_type;
            appLocalSettings.values['youtubeAccessToken'] = accessToken;
            log("parseAccessToken() Access Type: " + tokenType + ", Token: " + accessToken);
        } else {
            errorCallback("parseAccessToken() Wrong accessToken value!!", error, util.ERROR_TYPE.NO_OAUTH_TOKEN);
            return;
        }

        // Store refreshToken if exists
        if (jsonObj.refresh_token) {
            refreshToken = jsonObj.refresh_token;
            appLocalSettings.values['youtubeRefreshToken'] = refreshToken;
            log("parseAccessToken() Refresh Token: " + refreshToken);
        } else {
            if (!refreshToken) {
                errorCallback("parseAccessToken() Wrong refreshToken value!!", error, util.ERROR_TYPE.NO_REFRESH_TOKEN);
                return;
            }
        }

        // remember the user info for alreadty logged in
        getUserInfoAsync({ mine: true }).then(function (response) {
            appLocalSettings.values['youtubeUserInfo'] = JSON.stringify(response);
            complete();
        }, function (errorMsg) {
            logout();
            log("parseAccessToken() Get user information fail!!");
            if (errorMsg === util.ERROR_TYPE.NoLinkedYouTubeAccount) {
                error(util.ERROR_TYPE.NoLinkedYouTubeAccount);
            } else {
                error(util.ERROR_TYPE.NO_USER_INFO);
            }
        });
    }

    // Used in ytOptPage to list video category
    // See [VideoCategories: list] and try it
    // https://developers.google.com/youtube/v3/docs/videoCategories/list
    function getVideoCategoriesAsync(regionCode) {
        var urlParamSet = {
            "part": ytPART.snippet,
            "regionCode": (regionCode) ? regionCode : "US",
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.videoCategories + paramAsUrlString(urlParamSet);

        return sendYoutubeHttpRequestAsync(requestUrl);
    }

    // See [YouTube Data API - Resumable Uploads]
    // https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol
    function resumableUploadAsync(uploadParams, guidCallback) {
        return new WinJS.Promise(function (complete, error, progress) {
            // Prepare headers for Step 1. Start a resumable session
            var urlParamSet = {
                "uploadType": "resumable",
                "part": ytPART.snippet + "," + ytPART.status + "," + ytPART.contentDetails
            };

            var requestUrl = ytUploadAPI3 + ytAPI.videos + paramAsUrlString(urlParamSet);

            var uploadData = {
                "snippet": {
                    "title": uploadParams.title,
                    "description": uploadParams.description,
                    "tags": uploadParams.tags,
                    "categoryId": uploadParams.categoryId,
                },
                "status": {
                    "privacyStatus": (uploadParams.isPublic) ? "public" : "private",
                    "embeddable": true,
                    "license": "youtube"
                }
            }
            var uploadDataJSON = JSON.stringify(uploadData);

            var requestHeader;

            // We need to obtain the length of upload video
            // As said in [YouTube Data API - Resumable Uploads]
            // X-Upload-Content-Length – The number of bytes that will be uploaded in subsequent requests. Set this value to the size of the file you are uploading.
            return uploadParams.file.getBasicPropertiesAsync().then(function (fileProperty) {
                // Set Step 1's request header
                requestHeader = {
                    "Authorization": tokenType + ' "' + accessToken + '"',
                    "Content-Length": uploadDataJSON.length,
                    "Content-Type": "application/json; charset=UTF-8",
                    "X-Upload-Content-Length": fileProperty.size,//uploadParams.file.length,
                    "X-Upload-Content-Type": "video/*"
                };

                var xhrOptions = {
                    type: "POST",
                    url: requestUrl,
                    headers: requestHeader,
                    data: uploadDataJSON
                };
                // Sending request of Step 1 - Start a resumable session
                return WinJS.xhr(xhrOptions);
            }).then(function (response) {
                // Step 2 - Save the resumable session URI
                var uploadURL = response.getResponseHeader("Location");

                // uploadFileBackgroundAsync will count the Content-Length, should we omit this?
                var requestHeaderNext = {
                    "Authorization": tokenType + ' "' + accessToken + '"',
                    //"Content-Length": requestHeader["X-Upload-Content-Length"],
                    "Content-Type": requestHeader["X-Upload-Content-Type"],
                };

                // Step 3 - Upload the video file
                if (uploadURL) {
                    util.uploadFileBackgroundAsync(uploadParams.file, uploadURL, requestHeaderNext, guidCallback).then(complete, error, progress);
                } else {
                    errorCallback("resumableUploadAsync() Wrong Location value!", error, util.ERROR_TYPE.WRONG_UPLOAD_LOCATION);
                }
            }, function (err) {
                log("resumableUploadAsync() Request error: " + error.statusText);
                error(err);
            });
        });
    }

    // See http://kej.tw/flvretriever/
    // The parameter sts is put in the link of download file
    // E.g. for MostViewed "PSY - GANGNAM STYLE (강남스타일) M/V"
    //       (https://www.youtube.com/watch?v=9bZkp7q19f0)
    // We should get 
    // http://www.youtube.com/get_video_info?eurl=http%3A%2F%2Fkej.tw%2F&sts=16582&video_id=9bZkp7q19f0
    // --- Deprecated ---
    // >> Valid from 2013/11  // Expires End 2015/05
    // source code? http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20131107
    // --- Deprecated ---
    // >> Valid from 2015/05  // Expires End 2015/06
    // source code? http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20150505_0007
    // --- Deprecated ---
    // >> Valid from 2015/06  // Expires End 2015/06/24
    // source code? http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20150604_0050
    // --- Valid ---
    // >> Valid from 2015/06/24  // Expires End ??
    // source code? http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20150622_2338
    function getYouTubeUrlFromRequestAsync(videoId, bCheckFlash) {
        return new WinJS.Promise(function (onCompleteCallback, onErrorCallback, onProgressCallback) {
            var urlParamSet = {
                "video_id": videoId,
                "eurl": "http://localhost",
                // sts : important magic number parameter, lose this may make the signature fails
                "sts": kejParamHistory[0].sts
            };
            var requestUrl = kejVideoFetcherUrl + paramAsUrlString(urlParamSet);

            var requestHeader = getAccessTokenHeader(); // Header is used for watching MyUpload private videos

            WinJS.xhr({ type: "GET", url: requestUrl, headers: requestHeader}).then(
                function (data) {
                    if (data === null) {
                        log('getYouTubeUrlFromRequest() Parse error: no data');
                        return getYouTubeUrlFromPageAsync(videoId).then(onCompleteCallback, onErrorCallback, onProgressCallback);
                    }

                    var response = data.responseText;

                    var isFlashString = "as_launched_in_country=1";
                    // Is this dead code and unused parameter?
                    //If find this string in response text, it means that
                    // the video will launched with flash player in iFrame and cannot played in metro APP.
                    if (bCheckFlash && response.indexOf(isFlashString) === -1) {
                        var returnObj = { bUseIFrame: true };
                        util.callback(onCompleteCallback, returnObj);
                        return;
                    }

                    // Fetching the parameter, "status=xx"
                    // handle the case for "status=fail", No need to handle if "status=ok"
                    var statusString = peekParams(response, "status=", "&");

                    if (statusString === "fail") {
                        var reasonString = peekParams(response, "reason=", "&");
                        Debug.writeln("Fail for reason=" + reasonString);
                        return getYouTubeUrlFromPageAsync(videoId).then(onCompleteCallback, onErrorCallback, onProgressCallback);
                    } else { } // statusString === "ok"
                    
                    var itemData = parseUrlsClassic(response);
                    if (!itemData) {
                        return getYouTubeUrlFromPageAsync(videoId).then(onCompleteCallback, onErrorCallback, onProgressCallback);
                    }

                    // generating the watchable urls
                    var fmt = [];
                    var fmtUrl = [];
                    for (var k = 0; k < itemData.length; k++) {
                        fmt[k] = itemData[k].fmt;
                        if (itemData[k].fmt_url && itemData[k].fmt_sig) {
                            fmtUrl[k] = itemData[k].fmt_url + "&signature=" + itemData[k].fmt_sig;
                        } else {
                            fmtUrl[k] = itemData[k].fmt_url;
                        }
                    }

                    var returnObj = { "formatNum": fmt, "formatUrl": fmtUrl, "formatString": fmtStr };
                    if (fmt.length === 0) {
                        return getYouTubeUrlFromPageAsync(videoId).then(onCompleteCallback, onErrorCallback, onProgressCallback);
                    } else {
                        util.callback(onCompleteCallback, returnObj);
                    }
                },
                function (response) {
                    return getYouTubeUrlFromPageAsync(videoId).then(onCompleteCallback, onErrorCallback, onProgressCallback);
                });
        });
    }

    // parse the watchable urls from response of http://kej.tw/flvretriever
    function parseUrlsClassic(response) {
        // The format key is "url_encoded_fmt_stream_map"
        var fmtString = peekParams(response, "url_encoded_fmt_stream_map=", "&");

        // See [JavaScript decodeURIComponent() Function]
        // http://www.w3schools.com/jsref/jsref_decodeuricomponent.asp
        // fmtContent is the full content of fmtKey
        var fmtContent = decodeURIComponent(fmtString);
        var fmtLines = fmtContent.split(",");
        var items = [];

        // We parse the urls and put into items here
        for (var i = 0; i < fmtLines.length; i++) {
            var fmtSubLines = fmtLines[i].split("&");
            var item = {};
            var itag = -1;
            var type = "";
            // Fetching the important parameters, especially signature and url
            for (var j = 0; j < fmtSubLines.length; j++) {
                var keyIndex = fmtSubLines[j].indexOf("=");
                var key = fmtSubLines[j].substring(0, keyIndex + 1);
                var value = fmtSubLines[j].substring(keyIndex + 1);
                switch (key) {
                    case "itag=":
                        itag = parseInt(value, 10);
                        item.fmt = itag;
                        break;
                    case "url=":
                        item.fmt_url = decodeURIComponent(decodeURIComponent(value));
                        break;
                    case "sig=": // normal signature, use directly
                        item.fmt_sig = value;
                        break;
                    case "s=": // encrypted signature, need perform decryption
                        item.fmt_sig = SigHandlerAlternative(value);
                        break;
                    case "type=":
                        type = "(" + value + ")";
                        break;
                    default:
                        // omit fallback_host=... 
                        // omit quality=...
                        //Debug.writeln("Omit K=V : " + key + " = " + value);
                }
            }

            if (typeof fmtStr[itag] === "undefined") {
                fmtStr[itag] = type;
            }
            items.push(item);
        }

        return items;
    }

    function SigHandlerAlternative(s) {
        return SigHandlerAlternative_base(s, kejParamHistory[0].sCode);
    }

    // The base function used in Kej's FLV Retriever http://kej.tw/flvretriever
    // We may need to check the update for the function
    function SigHandlerAlternative_base(s, sCode) {
        // Perform swap sArray[y] <-> sArray[0]
        function swap(sArray, y) {
            var x = 0; // Swap sArray[y] <-> sArray[x]
            y = y % sArray.length; // Modulation location to within [0, sArray.length - 1]
            var ref = [sArray[y], sArray[x]];
            sArray[x] = ref[0];
            sArray[y] = ref[1];
            return sArray;
        }

        // String Reference http://www.w3schools.com/jsref/jsref_obj_string.asp
        // Array Reference http://www.w3schools.com/jsref/jsref_obj_array.asp
        var sChars = s.split(""); // Like Java's String.toCharArray(); i.e. sChar = s.toCharArray();

        for (var i = 0; i < sCode.length; i++) {
            var code = sCode[i];
            if (code > 0) {
                // Change key inside
                sChars = swap(sChars, code);
            } else if (code === 0) {
                // Reverse the key
                sChars = sChars.reverse();
            } else {
                // Cut the key's prefix
                sChars = sChars.slice(-code);
            }
        }

        return sChars.join("");
    }

    // --- Deprecated ---
    //"sts": 1586, // Deprecated >> Valid from 2013/11  // Expires End 2015/05
    // >> Valid from 2013/11  // Expires End 2015/05
    // See http://kej.tw/flvretriever/
    // source code? http://kej.tw/flvretriever/script/parse.youtube.fmt_url_map.js?v=20131107
    function SigHandlerAlternative_20131107(s) {
        // String Reference http://www.w3schools.com/jsref/jsref_obj_string.asp
        // Array Reference http://www.w3schools.com/jsref/jsref_obj_array.asp
        var sChars = s.split(""); // Like Java's String.toCharArray(); i.e. sChar = s.toCharArray();
        var t, x, y;

        x = 0, y = 52;
        // Step1 : swap char of s[0] <=> s[52]
        t = sChars[x];
        sChars[x] = sChars[y];
        sChars[y] = t;

        x = 62, y = 83;
        // Step2 : swap char of s[62] <=> s[83]
        t = sChars[x];
        sChars[x] = sChars[y];
        sChars[y] = t;

        // Step3 : truncate chars of first 3 chars and last 3 chars
        sChars = sChars.slice(3, sChars.length - 3);
        // Step4 : reverse the string s
        sChars = sChars.reverse();

        return sChars.join("");
    }

    // peek the watchable urls from source code of YouTube watch page
    function getYouTubeUrlFromPageAsync(videoId) {
        return new WinJS.Promise(function (complete, error) {
            var requestUrl = ytWatchUrl + "v=" + videoId;

            // Step 1: Fetch YouTube page source code by  XMLHttpRequest
            WinJS.xhr({ type: "GET", url: requestUrl }).then(
                function (data) {
                    if (data === null) {
                        errorCallback("getYouTubeUrlFromRequest() Parse error: no data", error, util.ERROR_TYPE.NO_RESPONSE_DATA);
                        return;
                    }

                    // Here is the YouTube page source code
                    var response = data.responseText;

                    // find inner-est object has the key url_encoded_fmt_stream_map
                    var fmtKey = "url_encoded_fmt_stream_map";
                    var fmtRegExp = /{[^{}]*url_encoded_fmt_stream_map[^{}]*}/g;

                    var matchedJSON = response.match(fmtRegExp);
                    // report error if we cannot find the fmtObject
                    if (matchedJSON === null) {
                        errorCallback("getYouTubeUrlFromPageAsync() Can not find mapping string!!! RegExp:/" + fmtRegExp.source + "/" + fmtRegExp.options, error, util.ERROR_TYPE.STRING_NOT_FOUND);
                        return;
                    }

                    // parse the urls from page's JSON object
                    var itemData = parseUrlsClassicByPage(matchedJSON[0], fmtKey);

                    // generating the watchable urls
                    var fmt = [];
                    var fmtUrl = [];
                    for (var k = 0; k < itemData.length; k++) {
                        fmt[k] = itemData[k].fmt;
                        if (itemData[k].fmt_url && itemData[k].fmt_sig) {
                            fmtUrl[k] = itemData[k].fmt_url + "&signature=" + itemData[k].fmt_sig;
                        } else {
                            fmtUrl[k] = itemData[k].fmt_url;
                        }
                    }

                    var returnObj = { "formatNum": fmt, "formatUrl": fmtUrl, "formatString": fmtStr };
                    if (fmt.length === 0) {
                        util.callback(error, util.ERROR_TYPE.NO_CLIP_PATH);
                    } else {
                        util.callback(complete, returnObj);
                    }
                },
                function (response) {
                    util.callback(error, util.ERROR_TYPE.REQUEST_FAIL);
                });
        });
    }

    // parse the watchable urls from YouTube page's JSON object
    function parseUrlsClassicByPage(response, fmtKey) {
        var data;
        try {
            data = JSON.parse(response);
        } catch (errorMsg) {
            errorCallback("sendYoutubeHttpRequestAsync() Parse error: " + errorMsg, error, util.ERROR_TYPE.JSON_PARSE_ERROR);
            return;
        }

        // fmtContent is the full content of fmtKey
        var fmtContent = data[fmtKey];
        var fmtLines = fmtContent.split(",");
        var items = [];

        // Parse the urls and put url,sig,fmt into items here
        for (var i = 0; i < fmtLines.length; i++) {
            var fmtSubLines = fmtLines[i].split("&");
            var item = {};
            var itag = -1;
            var type = "";
            // Fetch the important parameters, especially signature and url
            for (var j = 0; j < fmtSubLines.length; j++) {
                var keyIndex = fmtSubLines[j].indexOf("=");
                var key = fmtSubLines[j].substring(0, keyIndex + 1);
                var value = fmtSubLines[j].substring(keyIndex + 1);

                switch (key) {
                    case "itag=":
                        itag = parseInt(value, 10);
                        item.fmt = itag;
                        break;
                    case "url=":
                        item.fmt_url = decodeURIComponent(decodeURIComponent(value));
                        break;
                    case "sig=": // normal signature, use directly
                        item.fmt_sig = value;
                        break;
                    case "s=": // encrypted signature, need perform decryption
                        item.fmt_sig = SigHandlerAlternative(value);
                        break;
                    case "type=":
                        type = "(" + decodeURIComponent(value) + ")";
                        break;
                    default:
                        // omit fallback_host=... 
                        // omit quality=...
                        //Debug.writeln("Omit K=V : " + key + " = " + value);
                }
            }

            if (fmtStr[itag] == "undefined") {
                fmtStr[itag] = type;
            }
            items.push(item);
        }

        return items;
    }

    // Send the actual queries here, we will use the queryParams to deligate the query to separated function
    function sendQueryRequestAsync(queryParams) {
        return new WinJS.Promise(function (complete, error) {
            function errorForParam(required) {
                errorCallback("sendQueryRequest() Wrong parameters!! Required : " + required, error, util.ERROR_TYPE.WRONG_PARAM);
            }

            switch (queryParams.dataQueryType) {
                case queryType.SEARCH:
                    if (!queryParams.searchKey) {
                        errorForParam("queryParams.searchKey");
                        return;
                    }
                    return sendQuerySearchAsync(queryParams).then(complete, error);
                    break;
                case queryType.MOST_VIEWED:
                    return sendQueryMostViewAsync(queryParams).then(complete, error);
                    break;
                case queryType.MOST_POPULAR:
                    if (!ytCHART || !ytCHART.mostPopular) {
                        errorForParam("ytCHART.mostPopular ");
                        return;
                    }
                    return sendQueryMostPopularAsync(queryParams).then(complete, error);
                    break;
                case queryType.MY_UPLOADED_VIDEO:
                    return sendQueryMyUploadAsync(queryParams).then(complete, error);
                    break;
                case queryType.SUBSCRIPTION:
                    return sendQueryMySubscriptionAsync(queryParams).then(complete, error);
                    break;
                case queryType.FAVORITE:
                    return sendQueryMyFavoriteAsync(queryParams).then(complete, error);
                    break;
                case queryType.PLAYLIST:
                    return sendQueryMyPlaylistAsync(queryParams).then(complete, error);
                    break;
                case queryType.SUBSCRIPTION_SET:
                case queryType.MOVIE_TRAILER:
                    return sendQueryChannelUploadAsync(queryParams).then(complete, error);
                    break;
                case queryType.PLAYLIST_SET:
                    return sendQueryPlaylistItemsAsync(queryParams).then(complete, error);
                    break;
                default:
                    Debug.writeln("ERROR in param queryParams: " + JSON.stringify(queryParams));
                    complete();
            }
        });
    }

    // Fetch the information of video with videoId
    // See [Videos: list] and try it
    // https://developers.google.com/youtube/v3/docs/videos/list
    function getYouTubeVideoInfoAsync(videoId, isPrivate) {
        var urlParamSet = {
            "part": ytPART.snippet + "," + ytPART.contentDetails + "," + ytPART.status + "," + ytPART.statistics,
            "id": videoId,
            "key": appKey
        };

        var requestHeader = (isPrivate) ? getAccessTokenHeader() : null;
        var requestUrl = ytDataAPI3 + ytAPI.videos + paramAsUrlString(urlParamSet);

        return sendYoutubeHttpRequestAsync(requestUrl, requestHeader);
    }

    // Search the videos with q=<query>
    // See [Search requests]
    // https://developers.google.com/youtube/v3/migration-guide#search
    //
    // See [Search: list] and try it
    // https://developers.google.com/youtube/v3/docs/search/list
    function sendQuerySearchAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.id,
            "q": encodeURIComponent(queryParams.searchKey),
            "type": ytTYPE.video,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.search + paramAsUrlString(urlParamSet);

        return sendYoutubeHttpRequestAsync(requestUrl, null);
    }

    // Search the videos with q = "" and order = "viewCount"
    // See [Search requests]
    // https://developers.google.com/youtube/v3/migration-guide#search
    //
    // See [Search: list] and try it
    // https://developers.google.com/youtube/v3/docs/search/list
    function sendQueryMostViewAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.id,
            "order": ytORDER.viewCount,
            "type": ytTYPE.video,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.search + paramAsUrlString(urlParamSet);

        return sendYoutubeHttpRequestAsync(requestUrl, null);
    }

    // List the videos with chart = "mostPopular"
    // See [Retrieve most popular videos]
    // https://developers.google.com/youtube/v3/migration-guide#videos
    //
    // See [Videos: list] and try it
    // https://developers.google.com/youtube/v3/docs/videos/list
    function sendQueryMostPopularAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.id,
            "chart": ytCHART.mostPopular,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.videos + paramAsUrlString(urlParamSet);

        return sendYoutubeHttpRequestAsync(requestUrl, null);
    }

    // Fetch the channel's(user's) information by given option.channelId(option.mine=true)
    // See [Retrieve information about a channel]
    // https://developers.google.com/youtube/v3/migration-guide#channels
    //
    // See [Channels: list] and try it
    // https://developers.google.com/youtube/v3/docs/channels/list
    function getChannelInfoAsync(option) {
        var urlParamSet = null;
        var requestUrl = null;
        var requestHeader = null;
        if (!option) {
            Debug.writeln("Option expected >> ");
            return;
        } else if (option.mine) {
            urlParamSet = {
                "part": ytPART.snippet + "," + ytPART.contentDetails + "," + ytPART.statistics,
                "mine": option.mine,
                "key": appKey
            };

            requestHeader = getAccessTokenHeader();
            requestUrl = ytDataAPI3 + ytAPI.channels + paramAsUrlString(urlParamSet);
        } else if (option.channelId) {
            urlParamSet = {
                "part": ytPART.snippet + "," + ytPART.contentDetails + "," + ytPART.statistics,
                "id": option.channelId,
                "key": appKey
            };

            requestHeader = null;
            requestUrl = ytDataAPI3 + ytAPI.channels + paramAsUrlString(urlParamSet);
        } else {
            Debug.writeln(" Something wrong in Option >> option = " + JSON.stringify(option));
        }

        return sendYoutubeHttpRequestAsync(requestUrl, requestHeader);
    }

    // Fetch the channel's (result.item[0]) related playlist (use ytRELATED_PLAYLISTS)
    // See [Retrieve related videos]
    // https://developers.google.com/youtube/v3/migration-guide#videos
    // 
    // See [PlaylistItems] and try it
    // https://developers.google.com/youtube/v3/docs/playlistItems
    function sendQueryRelatedPlaylistItemsAsync(result, queryParams) {
        if (!queryParams.playlistKey) {
            Debug.writeln("[sendQueryRelatedPlaylistItemsAsync] Important playlist key lost!! ");
            return;
        }
        var playlistKey = queryParams.playlistKey;

        if (!result && !result.items[0] && !result.items[0].contentDetails) {
            Debug.writeln("[sendQueryRelatedPlaylistItemsAsync] Important item lost!! ");
            return;
        }

        var itemDetails = result.items[0].contentDetails;
        if (!itemDetails.relatedPlaylists && !itemDetails.relatedPlaylists[playlistKey]) {
            Debug.writeln("[sendQueryRelatedPlaylistItemsAsync] Important contentDetails lost!! ");
            return;
        }

        queryParams.playlistId = itemDetails.relatedPlaylists[playlistKey];
        return sendQueryPlaylistItemsAsync(queryParams);
    }

    // Fetch the user's uploaded videos by two steps
    // See [Retrieve a channel's uploaded videos]
    // https://developers.google.com/youtube/v3/migration-guide#videos
    //
    //   Step 1: Retrieve the playlist ID for the channel's uploaded videos
    //   See [Channels: list]
    //   https://developers.google.com/youtube/v3/docs/channels/list
    //
    //   Step 2: Retrieve the list of uploaded videos
    //   See [PlaylistItems]
    //   https://developers.google.com/youtube/v3/docs/playlistItems
    function sendQueryMyUploadAsync(queryParams) {
        return getChannelInfoAsync({ mine: true }).then(function (result) {
            queryParams.forMine = true;
            queryParams.playlistKey = ytRELATED_PLAYLISTS.uploads;
            return sendQueryRelatedPlaylistItemsAsync(result, queryParams);
        });
    }

    // Fetch the user's subscriptions (of channels)
    // See [Retrieve a channel's subscriptions]
    // https://developers.google.com/youtube/v3/migration-guide#subscriptions
    //
    // See [Subscriptions: list] and try it
    // https://developers.google.com/youtube/v3/docs/subscriptions/list
    function sendQueryMySubscriptionAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.snippet + "," + ytPART.contentDetails,
            "mine": true,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.subscriptions + paramAsUrlString(urlParamSet);
        var requestHeader = getAccessTokenHeader();

        return sendYoutubeHttpRequestAsync(requestUrl, requestHeader);
    }

    // Fetch the user's favorite videos by two steps
    // See [Retrieve a user's favorite videos]
    // https://developers.google.com/youtube/v3/migration-guide#favorites-retrieve-for-channel
    //
    //   Step 1: Retrieve the playlist ID for the channel's favorite videos
    //   See [Channels: list]
    //   https://developers.google.com/youtube/v3/docs/channels/list
    //
    //   Step 2: Retrieve the list of favorite videos
    //   See [PlaylistItems]
    //   https://developers.google.com/youtube/v3/docs/playlistItems
    function sendQueryMyFavoriteAsync(queryParams) {
        return getChannelInfoAsync({ mine: true }).then(function (result) {
            queryParams.forMine = true;
            queryParams.playlistKey = ytRELATED_PLAYLISTS.favorites;
            return sendQueryRelatedPlaylistItemsAsync(result, queryParams);
        });
    }

    // Fetch user's playlists
    // See [Retrieve current user's playlists]
    // https://developers.google.com/youtube/v3/migration-guide#playlists-retrieve-for-current-user
    // 
    // See [Playlists: list] and try it
    // https://developers.google.com/youtube/v3/docs/playlists/list
    function sendQueryMyPlaylistAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.snippet + "," + ytPART.contentDetails,
            "mine": true,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.playlists + paramAsUrlString(urlParamSet);
        var requestHeader = getAccessTokenHeader();

        return sendYoutubeHttpRequestAsync(requestUrl, requestHeader);
    }


    // Fetch the user's uploaded video by two steps
    // See [Retrieve a channel's uploaded videos]
    // https://developers.google.com/youtube/v3/migration-guide#videos-retrieve-uploads
    //
    //   Step 1: Retrieve the playlist ID for the channel's uploaded videos
    //   See [Channels: list]
    //   https://developers.google.com/youtube/v3/docs/channels/list
    //
    //   Step 2: Retrieve the list of uploaded videos
    //   See [PlaylistItems]
    //   https://developers.google.com/youtube/v3/docs/playlistItems
    function sendQueryChannelUploadAsync(queryParams) {
        return getChannelInfoAsync({ channelId: queryParams.channelId }).then(function (result) {
            queryParams.forMine = false;
            queryParams.playlistKey = ytRELATED_PLAYLISTS.uploads;
            return sendQueryRelatedPlaylistItemsAsync(result, queryParams);
        });
    }

    // List the videos of specific playlist ID
    // See [PlaylistItems: list] and try it
    // https://developers.google.com/youtube/v3/docs/playlistItems/list
    function sendQueryPlaylistItemsAsync(queryParams) {
        var urlParamSet = {
            "part": ytPART.snippet + "," + ytPART.status,
            "playlistId": queryParams.playlistId,
            "maxResults": queryParams.maxResults,
            "pageToken": queryParams.nextPageToken,
            "key": appKey
        };

        var requestUrl = ytDataAPI3 + ytAPI.playlistItems + paramAsUrlString(urlParamSet);
        var requestHeader = (queryParams.forMine) ? getAccessTokenHeader() : null;

        return sendYoutubeHttpRequestAsync(requestUrl, requestHeader);
    }

    return {
        // Set client ID, client secret and App key
        //
        // Client ID for native application > CLIENT ID, CLIENT SECRET
        // Key for browser applications > API KEY
        //
        // See [Google Developer Console > API Project > APIs & auth > Credentials > OAuth]
        // https://console.developers.google.com/project
        setApiKey: function (oauth_consumer_key, oauth_consumer_secret, oauth_app_key) {
            clientId = oauth_consumer_key;
            clientSecret = oauth_consumer_secret;
            appKey = oauth_app_key;
            appLocalSettings.values['ytClientId'] = clientId;
            appLocalSettings.values['ytClientSecret'] = clientSecret;
            appLocalSettings.values['ytAppKey'] = appKey;
        },

        //Check login status
        isLoginAsync: function () {
            return new WinJS.Promise (function (onCompleteCallback, onErrorCallback) {
                if (refreshToken) {
                    getAccessWebAuthAsync().then( function () {
                        util.callback(onCompleteCallback, { "isLogin": true });
                    }, function () {
                        util.callback(onErrorCallback, { "isLogin": false });
                    });
                }
                else {
                    util.callback(onErrorCallback, { "isLogin": false });
                }
            });
        },

        //log out : delete the refresh token and access token
        logout: logout,

        ytID_KIND: ytID_KIND,
        ytVideoStatus: ytVideoStatus,
        queryType: queryType,
        pageTokens: pageTokens,

        // Login (oauth 2.0)
        // See https://developers.google.com/youtube/v3/guides/authentication
        // Step 1: Obtain an access token
        // See MSDN [WebAuthenticationBroker class]
        // http://msdn.microsoft.com/en-us/library/windows/apps/windows.security.authentication.web.webauthenticationbroker.aspx
        launchWebAuthAsync: function () {
            return new WinJS.Promise(function (complete, error) {
                var paramsOAuth = {
                    "client_id": clientId,
                    "redirect_uri": gglOAuth2_0.RedirectUri,
                    "response_type": "code",
                    "scope": gglOAuth2_0.Scope.Manage + " " + gglOAuth2_0.Scope.View
                }

                var OAuthUrl = gglOAuth2_0.ServerUrl + paramAsUrlString(paramsOAuth);

                try {
                    var invokeUri = new Windows.Foundation.Uri(OAuthUrl);
                    var finishUri = new Windows.Foundation.Uri(gglOAuth2_0.Approval);
                    authenticatePromise = AuthWeb.WebAuthenticationBroker.authenticateAsync(AuthWeb.WebAuthenticationOptions.useTitle, invokeUri, finishUri).then(
                        function (response) {
                            authenticatePromise = null;
                            onWebAuthSuccessAsync(response).then(complete, error);
                        },
                        function (err) {
                            authenticatePromise = null;
                            // Web Authentication Error handling
                            log("Error returned by WebAuth broker. Error Number: " + err.number + " Error Message: " + err.message);
                            if (err.message === 'Canceled') {
                                error(util.ERROR_TYPE.USER_CANCEL);
                            } else {
                                error(util.ERROR_TYPE.WEBAUTH_ERROR);
                            }
                        });
                } catch (err) {
                    errorCallback("launchWebAuth() Error launching WebAuth" + err, error, util.ERROR_TYPE.WEBAUTH_ERROR);
                    return;
                }
            });
        },

        cancelWebAuth: function () {
            if (authenticatePromise) {
                try {
                    authenticatePromise.cancel();
                } catch (exception) {
                    log('cancelWebAuth() exception: ' + exception);
                }
                authenticatePromise = null;
            }
        },

        //Get my information with request.
        getUserInfoAsync: getUserInfoAsync,

        getUploadCategoryAsync: getVideoCategoriesAsync,

        resumableUploadAsync: resumableUploadAsync,

        //Get clip original playback path
        //Kernel will check if video will launched with flashplayer in iFrame when bCheckFlash be set as true.
        getYouTubeUrlAsync: getYouTubeUrlFromRequestAsync,

        getYouTubeVideoInfoAsync: getYouTubeVideoInfoAsync,

        //If user has logged in, this function can return the user information without send request.
        getCurrentUserInfo: function () {
            var youtubeUserInfo = appLocalSettings.values['youtubeUserInfo'];
            return youtubeUserInfo ? JSON.parse(youtubeUserInfo) : {};
        },

        sendQueryRequestAsync: sendQueryRequestAsync,

        //Upload support type
        getSupportedType: function () {
            // Also see https://support.google.com/youtube/?hl=zh-Hant#topic=4355169
            // Ref: http://support.google.com/youtube/bin/answer.py?hl=en&answer=55744
            // Sync \\clt-qade-acer\Document\Acer NB\2011 projects\SNS3.0\Functional Spec\3.0\Acer NB SocialNetworks3.0 PES v0.9  (page 22)   2012/05/21
            return [".3gp2", ".3gp", ".3gpp", ".asf", ".avi", ".dat", ".flv", ".m4v", ".mp4v", ".mkv", ".mod", ".mov", ".mp4", ".mpe", ".mpeg", ".mpeg4", ".mpg", ".m1v", ".m2v",
                ".div", ".divx", ".vro", ".tod", ".vob", ".wmv", ".rmvb", ".rm", ".m2ts", ".ts", ".m2t", ".mts", ".tts", ".tsp", ".tp", ".dvr-ms", ".wm"];
        },
    };
})();
