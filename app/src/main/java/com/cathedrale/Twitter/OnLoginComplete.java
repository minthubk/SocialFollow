/**
 * 
 */
package com.cathedrale.Twitter;

import org.brickred.socialauth.Profile;

/**
 * @author aspire
 * All rights recevied  Jul 16, 2014
 * 
 * TwitterLoginCompleted is an interface to check twitter login.
 * 
 */
public interface OnLoginComplete {

	public void twitterCompleted(Profile result);

}
