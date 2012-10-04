
-module(tut).

-export([reverse/1,list_max/1,double/1,factorial/1, area/1,fac/1]).


reverse(l) ->
	reverse(l,[]).

reverse(l,a)->
	case l of 
		[] ->
			a;
		[Head|Rest] ->
			reverse(Rest, [Head|a])
	end.

list_max([Head|Rest]) ->
   list_max(Rest, Head).

list_max([], Res) ->
    Res;
list_max([Head|Rest], Result_so_far) 
  when Head > Result_so_far ->
    list_max(Rest, Head);
list_max([Head|Rest], Result_so_far)  ->
    list_max(Rest, Result_so_far).


double(X) ->
	times(X, 2).
times(X, N) ->
	X * N.

factorial(0) -> 1;
factorial(N) -> N * factorial(N-1).

area({square, Side}) ->
       Side * Side;
area({circle, Radius}) ->
       % almost :-)
       3 * Radius * Radius;
area({triangle, A, B, C}) ->
       S = (A+B+C)/2,
       math:sqrt(S*(S-A)*(S-B)*(S-C));
area(Other) ->
       {invalid_object, Other}.


fac(0) -> 1;
fac(N) -> N * fac(N-1).

