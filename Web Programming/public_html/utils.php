<?php

function checkShot($x, $y, $radius) : bool {
    //Rectangle Check
    if(-$radius <= $x && $x <= 0 && 0 <= $y && $y<= $radius/2) return true;
    //Triangle Check
    if(-$radius <= $x && $x <= 0 && (-0.5*$x - $radius/2)<= $y && $y <= 0) return true;
    //Arc Check
    if($x>0 && $y>0 && ($x**2 + $y**2 <= $radius)) return true;

     return false;
}

readonly class Shot {
    function __construct(public bool $isHit, public int $x, public float $y, public int $radius, public string $timestamp)
    {}
}


?>