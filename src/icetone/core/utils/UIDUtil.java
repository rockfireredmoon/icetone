/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.utils;

import java.util.UUID;

/**
 *
 * @author t0neg0d
 */
public class UIDUtil {
	public static String getUID() {
		return UUID.randomUUID().toString();
	}
}
