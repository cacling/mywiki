-module(counter).
-export([start/0, stop/1]).
-export([inc/1, dec/1, reset/1, current/1]).

start() ->
    spawn(fun() -> loop(0) end).

stop(Pid) ->
    Pid ! stop.

inc(Pid) ->
    Pid ! inc.

dec(Pid) ->
    Pid ! dec.

reset(Pid) ->
    Pid ! reset.

current(Pid) ->
    Pid ! {current, self()},
    receive
        Any -> Any
    end.

loop(Count) ->
    receive
        stop -> ok;
        inc -> loop(Count + 1);
        dec -> loop(Count - 1);
        reset -> loop(0);
        {current, Pid} ->
            Pid ! Count,
            loop(Count)
    end.