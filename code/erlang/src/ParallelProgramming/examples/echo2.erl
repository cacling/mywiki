-module(echo2).
-export([go/0, loop/0]).

go() ->
	register(echo2, spawn(echo2,loop,[])),
	echo2 ! {self(), hello},
	receive
		{_Pid, Msg} ->
			io:format("~w~n", [Msg])
	end.


loop() ->
	receive
		{From, Msg} ->
			From ! {self(), Msg},
			loop();
		stop ->
			true
	end.
