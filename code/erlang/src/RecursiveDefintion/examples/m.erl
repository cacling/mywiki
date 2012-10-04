
-module(m).

-export([fact/1,drp/2,tak/2,reverse/1,app/2,fib/1,pow/2]).

%fac
fact(N) when N>0 ->  
    N * fact(N-1);  
fact(0) ->         
    1.  

% power function
pow(N,0) -> 1;
pow(N,M) ->
	if 
		(M rem 2) == 0 ->
			Q1 =  pow(N,(M div 2)),
			Q1*Q1;
		(M rem 2) == 1 ->
			Q2 =  pow(N,(M div 2)),
			Q2*Q2*N
	end.


%drop the first n elements of a list
drp(0,L) -> L;
drp(N,[H|T]) -> drp((N-1),T).

%reverse a list
reverse(L) ->
	reverse(L,[]).
reverse(L,R)->
	case L of 
		[] ->
			R;
		[H|T] ->
			reverse(T, [H|R])
	end.

%take the first n elements of a list
tak(0,L) ->
	[];
tak(N,[H|T]) -> [H|tak((N-1),T)].


%append two lists
app(L1,L2) ->
	app(L1,L2,[]);
app([],L) ->L;
app(L,[]) ->L.

app([],[],R) -> R;
app([],L2,R) -> app(R,[],L2);
app([H|T],L2,R) ->
	app(T,L2,[H|R]).

% Fibonacci
% 0 if n is 0
% 1 if n is 1
% fib(n-1) + fib(n-2) otherwise

fib(N) ->
	fib(N,0,1).
fib(0,A,B) ->
	A;
fib(1,A,B) ->
	B;
fib(N,A,B) ->
	fib((N-1),B,(A+B)).



