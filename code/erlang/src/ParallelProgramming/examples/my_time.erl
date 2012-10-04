-module(my_time).
-export([send_after/2, sleep/1, send/3]).

send_after(Time, Msg) ->
	spawn(my_time, send, [self(), Time, Msg]),
	receive
		Msg -> 
			io:format("~w~n", [Msg])
	end.

send(Pid, Time, Msg) ->
	receive
	after Time -> 
			Pid ! Msg
	end.

sleep(T) ->
	receive
	after T 
			-> true
	end.