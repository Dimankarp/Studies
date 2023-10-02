
export const X_ALLOWED_ARR = [-4, -3, -2, -1, 0, 1, 2, 3, 4];
export const Y_MAX = 5;
export const Y_MIN = -5;
export const RADIUS_ALLOWED_ARR = [1, 2, 3, 4];
export function xIsValid(input){
    let val = Number(input);
    if(!isNaN(val) && X_ALLOWED_ARR.includes(val))return true;
    return false;
}

export function yIsValid(input){
    let val = Number(input);
    if(!isNaN(val) && val >= Y_MIN && val <= Y_MAX){return true; }
    return false;
}
