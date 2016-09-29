# NimBinary
A UDP version of the game of Nim

This repository contains two programs: Nim and NimServer. When run together, they will enable two players to play the game of Nim.

## Background
This set of programs was part of a project for CSCI 251 - Concepts of Parallel and Distributed Systems. The GUI was provided by Professor Alan Kaminsky.

## Usage
<B>java NimServer (serverhost) (serverport)</B>

serverhost: IP address of the server

serverport: Port to which connections will be made

<B>java Nim (serverhost) (serverport) (clienthost) (clientport) (playername)</B>

serverhost: IP address of the server

serverport: Port to which the connection is made

clienthost: IP address of the client

clientport: Port to which the connection is made

playername: The player's alias, must not contain whitespace

## Example Run
In one window:

<B>java NimServer localhost 5680</B>

In a second window:

<B>java Nim localhost 5680 localhost 5681 Alice</B>

In a third window:

<B>java Nim localhost 5680 localhost 5682 Bob</B>


On a player's turn, the game tokens will be colored red. When they select a token, the game will remove all tokens above and including the one selected. The game ends when the last token is selected. The game session ends when a player closes their window. The server will run until it is terminated.
