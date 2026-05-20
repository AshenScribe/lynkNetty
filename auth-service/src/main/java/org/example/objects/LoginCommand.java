package org.example.objects;

public sealed interface LoginCommand permits BasicLoginCommand, TokenLoginCommand {
	String executeAuthentication();
}