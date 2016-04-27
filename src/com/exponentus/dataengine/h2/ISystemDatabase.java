package com.exponentus.dataengine.h2;

import java.util.ArrayList;

import com.exponentus.legacy.User;

public interface ISystemDatabase {

	User getUser(long docID);

	ArrayList<User> getAllUsers(String condition, int start, int end);

}
