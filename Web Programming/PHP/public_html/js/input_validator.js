
export const X_ALLOWED_ARR = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
export const Y_MAX = 5;
export const Y_MIN = -3;
export const RADIUS_ALLOWED_ARR = [1, 2, 3, 4, 5];
export function xIsValid(input){
    let val = Number(input);
    if(val !== NaN && X_ALLOWED_ARR.includes(val))return val; 
    return X_ALLOWED_ARR[0];
}

export function yIsValid(input){
    let val = Number(input);
    if(val !== NaN && val >= Y_MIN && val <= Y_MAX){return true; }
    return false;
}
