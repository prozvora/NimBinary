# NimBinary
A UDP version of the game of Nim

This repository contains two programs: Nim and NimServer. When run together, they will enable two players to play the game of Nim.

## Background
This set of programs was part of a project for CSCI 251 - Concepts of Parallel and Distributed Systems. The GUI was provided by Professor Alan Kaminsky.

## Usage
java NimServer (serverhost) (serverport)

serverhost: IP address of the server

serverport: Port to which connections will be made

java Nim (serverhost) (serverport) (clienthost) (clientport) (playername)

serverhost: IP address of the server

serverport: Port to which the connection is made

clienthost: IP address of the client

clientport: Port to which the connection is made

playername: The player's alias, must not contain whitespace

## Example Run
In one window:

java NimServer localhost 5680

In a second window:

java Nim localhost 5680 localhost 5681 Alice

In a third window:

java Nim localhost 5680 localhost 5682 Bob


On a player's turn, the game tokens will be colored red. When they select a token, the game will remove all tokens above and including the one selected. The game ends when the last token is selected. The game session ends when a player closes their window. The server will run until it is terminated.
