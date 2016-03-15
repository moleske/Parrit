var { setLoginErrorCreator } = require('dashboard/actions/creators/dashboardCreators.js');
var { postNewWorkspaceAndDo, postLoginAndRedirect } = require('shared/helpers/databaseHelpers.js');

export function createWorkspaceThunk(name, password) {
    postNewWorkspaceAndDo(name, password, function() {
        postLoginAndRedirect(name, password);
    });
}

export function loginThunk(name, password) {
    return function(dispatch, getState) {
        postLoginAndRedirect(name, password, function(errorMessage) {
            dispatch(setLoginErrorCreator(errorMessage));
        });
    }
}


