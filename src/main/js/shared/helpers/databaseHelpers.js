const Axios = require('axios');

Axios.defaults.headers.post['Content-Type'] = 'application/json';

function postProjectAndDo(project, successCallback, errorCallback) {
    Axios.post('/api/project', project)
        .then(function (response) {
            successCallback(response.data);
        })
        .catch(function onError(error) {
            errorCallback(error.response.status);
        });
}

function postNewProjectAndDo(name, password, successCallback, errorCallback) {
    Axios.post('/api/project/new', {name, password})
        .then(function onSuccess(response) {
            successCallback(response.data);
        })
        .catch(function onError(error) {
            errorCallback(error.response.status);
        });
}

function postProjectPairingAndDo(projectId, successCallback) {
    Axios.post('/api/project/' + encodeURIComponent(projectId) + '/pairing')
        .then(function (response) {
            successCallback(response.data);
        })
}

function getRecommendedPairingAndDo(projectId, successCallback) {
    Axios.get('/api/project/' + encodeURIComponent(projectId) + '/pairing/recommend')
        .then(function (response) {
            successCallback(response.data);
        });
}

function postLoginAndRedirect(name, password, errorCallback) {
    Axios.post('/login', {name: name, password: password})
        .then(function onSuccess(response) {
            window.location.href = response.data;
        }).catch(function onError(error) {
            errorCallback(error.response.status);
        });
}

function postAddNewPersonAndDo(projectId, name, successCallback, errorCallback) {
    Axios.post('/api/project/' + encodeURIComponent(projectId) + '/addPerson', {name: name})
        .then(function (response) {
            successCallback(response.data);
        }).catch(function onError(error) {
            errorCallback(error.response.status);
        });
}

function getPairingHistoryAndDo(projectId, successCallback) {
    Axios.get('/api/project/' + encodeURIComponent(projectId) + '/pairing/history')
        .then(function (response) {
            successCallback(response.data);
        });
}

function postLogout() {
    Axios.post('/logout/project')
        .then(function onSuccess() {
            window.location.href = "/";
        });
}

module.exports = {
    postProjectAndDo,
    postNewProjectAndDo,
    postProjectPairingAndDo,
    getRecommendedPairingAndDo,
    postLoginAndRedirect,
    postAddNewPersonAndDo,
    getPairingHistoryAndDo,
    postLogout
};
