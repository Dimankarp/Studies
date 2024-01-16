export function getCookies(){
    return document.cookie.split("; ").map((value, index, array)=>{
        let {key, val} = value.split("=")
        return {key: key, val: val}
    })
}

export function getCookie(name){
    let cookie = document.cookie.match(new RegExp(`${name}=.*?(?=;|$)`))
    if(cookie !== null){
        let [key, val] = cookie[0].split("=");
        return {key: key, val: val}
    }
    return undefined;
}

export function setCookie(name, value) {
    let cookies = getCookies()
    document.cookie = `${name}=${value};`
}