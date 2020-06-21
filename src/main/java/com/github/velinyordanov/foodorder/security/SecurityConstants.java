package com.github.velinyordanov.foodorder.security;

public class SecurityConstants {
    public static final String SECRET = "Mm4fzJx?2FafFV[cq.b?-}#~@k!C:gS}~w7xP2%q\"zYA-'qBad9:<95t=DxVVM{,J7//W$]G[E+rzbswY~H,Qmj9Rq.Av~%YVnR~\"MuVH`L>b]E>yxHCzdZu&[KrKmE\"8T^N'>@qQ)?{z,NF4Q2\"d;2Yz?zRg(t3]ccJ-^]+6/7!fZ:BSz%Z'^%$JzhY;^e^XBGhq6TeF4Y.~zTJJyD)Z\"xa<K5(DXpJDL3/qj4mxrG77~x7NF9n-PhXg)*VB$~Z";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/register";
}