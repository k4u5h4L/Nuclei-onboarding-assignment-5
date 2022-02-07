package com.gonuclei.allcaughtup.constant;

public class Constants {

  final public static long JWT_VALIDITY = 5 * 60 * 60;

  final public static String NOT_AUTHENTICATED_MESSAGE = "You don't seem to be logged in";
  final public static String USER_DISABLED_MESSAGE = "This User seems to be disabled";
  final public static String INVALID_CREDENTIALS_MESSAGE = "Credentials given were invalid";
  final public static String EMAIL_ALREADY_EXISTS_MESSAGE = "User with this email already exists";
  final public static String USER_NOT_FOUND_MESSAGE = "User not found with username: ";
  final public static String SUBSCRIPTION_NOT_FOUND_MESSAGE =
      "Subscription with that ID does not exists";
  final public static String SUBSCRIPTION_ALREADY_EXISTS_MESSAGE =
      "You seem to have already subscribed to this subscription";

  final public static String USER_IS_NOT_SUBSCRIBED_MESSAGE =
      "You don't seem to be subscribed to this subscription";
  final public static String UNABLE_TO_GET_JWT_MESSAGE = "Unable to get JWT Token";
  final public static String JWT_TOKEN_EXPIRED_MESSAGE = "JWT Token has expired";
  final public static String JWT_DOES_NOT_BEGIN_WITH_MESSAGE =
      "JWT Token does not begin with Bearer String";
  final public static String KAFKA_TOPIC_NAME = "allcaughtup";
  final public static String KAFKA_GROUP_NAME = "emaillisteners";
  final public static String INTERNAL_SERVER_ERROR = "Internal Server Error";
  final public static String INVALID_ORDER_MESSAGE =
      "'order' field should be either 'asc' or 'desc'";
}
