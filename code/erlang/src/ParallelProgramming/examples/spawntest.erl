%% 1> spawntest:start(1000000). 
%% 3.58599e+5


-module(spawntest).
-export([start/1,task/0, loop/2]).


start(NumberOfThreads) ->
    Start = erlang:now(),
	LooperPID = spawn(spawntest, loop, [self(), NumberOfThreads]),
    create_proc(LooperPID, NumberOfThreads),
	receive
		ok -> ok
	end,
    Stop = erlang:now(),
	NumberOfThreads / time_diff(Start, Stop).

create_proc(_, 0) -> 0;
create_proc(LooperPid, Num) ->
	NPid = spawn(spawntest, task, []),
	NPid ! {LooperPid, ok},
	create_proc(LooperPid, Num-1).

task() ->
	receive {LooperPid, ok} -> LooperPid ! ok
	end.

loop(MainPid, 0) -> MainPid ! ok;
loop(MainPid, Num) ->
	receive 
		ok -> loop(MainPid, Num-1)
	end.

%Timestamp = Mega * 1000000  + Sec  + Micro / 1000000
time_diff({A1,A2,A3}, {B1,B2,B3}) ->
    (B1 - A1) * 1000000 + (B2 - A2) + (B3 - A3) / 1000000.0.

