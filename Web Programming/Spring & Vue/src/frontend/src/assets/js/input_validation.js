
// Shot input validation
export const X_MIN = -2
export const X_MAX = 2
const X_STEP = 0.5

export const Y_MAX = 5;
export const Y_MIN = -3;

const RADIUS_MIN = -2
const RADIUS_MAX = 2
const RADIUS_STEP = 0.5

export const X_VALID_VALS = Array((X_MAX-X_MIN)/X_STEP+1).fill().map((element, index) => X_MIN + index*X_STEP)
export const RADIUS_VALID_VALS = Array((RADIUS_MAX-RADIUS_MIN)/RADIUS_STEP+1).fill().map((element, index) => RADIUS_MIN + index*RADIUS_STEP)

export function xIsValid(input) {
    let val = Number(input);
    if (!isNaN(val) && X_VALID_VALS.includes(val)) return true;
    return false;
}

export function yIsValid(input) {
    let val = Number(input);
    if (!isNaN(val) && (input >= Y_MIN && input <= Y_MAX)) return true;
    return false;
}

export function radiusIsValid(input) {
    let val = Number(input);
    if (!isNaN(val) && RADIUS_VALID_VALS.includes(val) && val > 0) return true;
    return false;
}


//Uer crdentials validation

export function usernameIsValid(username) {
    let usernameStr = String(username);
    if (usernameStr.length > 0 && usernameStr.length <= 35 && /^[\x00-\x7F]*$/.test(usernameStr)) return true; //The regex is tests for ASCII-only.
    else return false;
}

export function passwordIsValid(password) {
    let passwordStr = String(password);
    if (passwordStr.length > 10 && passwordStr.length <= 35) return true;
    else return false;
}

export function passwordsMatch(password, repeat_password) {
    return password === repeat_password;
}


