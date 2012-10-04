-module(examples).

-export([fac/1,len/1,len2/1,sum/1,sum2/1,avg/1, avg2/1,append/2,
reverse/1,doubleAll/1,revAll/1,map/2]).

fac(0) -> 1;
fac(N) when N > 0 
  	   -> N * fac(N-1).

sum([]) -> 0;
sum([Head|Tail]) -> Head + sum(Tail).

sum2(List) -> sum_acc(List,0).
sum_acc([],Sum) -> Sum;
sum_acc([Head|Tail],Sum) -> sum_acc(Tail, Head+Sum).

len([]) -> 0;
len([_|Tail]) -> 1 + len(Tail).  

len2(List) -> len_acc(List,0).
len_acc([], Length) -> Length;
len_acc([_|Tail],Length) -> len_acc(Tail, Length+1).

avg(List) -> sum(List) / len(List).

avg2(List) -> avg_acc(List,0,0).
avg_acc([],Sum,Length) -> Sum / Length;
avg_acc([H|T],Sum,Length) -> avg_acc(T,Sum+H,Length+1).

append([],List2) ->  List2;
append([Head|Tail],List2) -> [Head | append(Tail,List2)].

reverse([]) -> [];
reverse([Head|Tail]) -> append(reverse(Tail),[Head]).

doubleAll([]) -> [];
doubleAll([X|Xs]) -> [X*2|doubleAll(Xs)]. 

revAll([]) -> [];
revAll([X|Xs]) -> [1/X|revAll(Xs)]. 

map(_,[]) -> [];
map(F,[X|Xs]) -> [F(X) | map(F,Xs)].
