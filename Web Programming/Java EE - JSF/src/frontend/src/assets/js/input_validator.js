
 const X_MIN = -5
 const X_MAX = 5
 const Y_MAX = 3;
 const Y_MIN = -3;

 const RADIUS_MIN = 1
 const RADIUS_MAX = 4
 function xIsValid(input){
    let val = Number(input);
    if(!isNaN(val) && (input >= X_MIN && input<=X_MAX))return true;
    return false;
}

 function yIsValid(input){
    let val = Number(input);
    if(!isNaN(val) && (input >= Y_MIN && input<=Y_MAX))return true;
    return false;
}
