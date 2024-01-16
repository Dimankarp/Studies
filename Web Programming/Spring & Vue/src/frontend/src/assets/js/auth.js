
export async function onStartupAuthorizationCycle(){
    if(!isLoggedIn() || ! (await tryRefreshingToken())){
        logout();
    }
}

export async function launchWithAuthCycle(protectedFunction){
    if(!isLoggedIn()){
        logout();
        return null;
    } else {
        if(await protectedFunction()){
            if(await tryRefreshingToken()){
                protectedFunction();
            } else{
                logout();
            }
        }
    }
}

export function tryRefreshingToken() {
    return fetch("/api/auth/token", {
        method: "GET",
        credentials: "same-origin"
    }).then(async (response) => {
        if (response.ok) {
            let token = await response.text()
            window.localStorage.setItem("token", token)
            return true;
        }
        else {
            return false;
        }
    })
}

export function getToken() {
    return window.localStorage.getItem("token")
}

export function getUsername() {
    return window.localStorage.getItem("username")
}

export function isLoggedIn() {
    return getToken() != null && getUsername != null;
}

export function registerToken(token, username) {
    window.localStorage.setItem("token", token)
    window.localStorage.setItem("username", username)
}

export function logout() {
    return fetch("/api/auth/logout", {
        method:"POST",
        credentials: "same-origin"
    }).finally(()=>{
        window.localStorage.clear()
        window.location.replace("/")
    })

}