-module(exercises).

-export([sum/1,sum2/2,create/1,create2/1,qsort1/1]).

%% 编写函数sum/1，给定一个正整数N，其返回的是1~N的整数和
sum(0) -> 0;
sum(N) when N > 0 -> N + sum(N-1).

%% 编写函数sum/2，给定两个正整数N和M，其中N<=M，其返回的是N~M的整数和。
%% 如果 N>M，程序异常终止。
sum2(N, M) when N==M -> M;
sum2(N, M) when N<M -> N + sum2(N+1,M);
sum2(N, M) when N>M -> throw({error,{invalid_parameters, "Invalid parameter N>M", N, M}}).

%% 编写一个返回格式为[N,N-1,...,2,1]和[1,2,...,N-1,N]的列表的函数
create(1) -> [1];
create(N) when N > 1 -> [N | create(N-1)].

create2(1) -> [1];
create2(N) when N > 1 -> create2(N-1) ++ [N].

%% 快速排序
qsort1([]) -> [];
qsort1([H | T]) -> 
    qsort1([ X || X <- T, X < H ]) ++ [H] ++ qsort1([ X || X <- T, X >= H ]).

qsort2([]) -> [];
qsort2([H | T]) ->
    {Less, Equal, Greater} = part(H, T, {[], [H], []}),
    qsort2(Less) ++ Equal ++ qsort2(Greater).

part(_, [], {L, E, G}) ->
    {L, E, G};
part(X, [H | T], {L, E, G}) ->
    if
        H < X ->
            part(X, T, {[H | L], E, G});
        H > X ->
            part(X, T, {L, E, [H | G]});
        true ->
            part(X, T, {L, [H | E], G})
    end.

qsort3([]) ->
    [];
qsort3([H | T]) ->
    qsort3_acc([H | T], []).
qsort3_acc([], Acc) ->
    Acc;
qsort3_acc([H | T], Acc) ->
    part_acc(H, T, {[], [H], []}, Acc).
part_acc(_, [], {L, E, G}, Acc) ->
    qsort3_acc(L, (E ++ qsort3_acc(G, Acc)));
part_acc(X, [H | T], {L, E, G}, Acc) ->
    if
        H < X ->
            part_acc(X, T, {[H | L], E, G}, Acc);
        H > X ->
            part_acc(X, T, {L, E, [H | G]}, Acc);
        true ->
            part_acc(X, T, {L, [H | E], G}, Acc)
    end.

%% 合并排序

%% DB习题
%% 索引习题
%% 文本处理习题

