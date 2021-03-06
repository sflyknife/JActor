/*
 * Copyright 2011 Bill La Forge
 *
 * This file is part of AgileWiki and is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (LGPL) as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * or navigate to the following url http://www.gnu.org/licenses/lgpl-2.1.txt
 *
 * Note however that only Scala, Java and JavaScript files are being covered by LGPL.
 * All other files are covered by the Common Public License (CPL).
 * A copy of this license is also included and can be
 * found as well at http://www.opensource.org/licenses/cpl1.0.txt
 */

/**
 * <p>
 * The apc package implements Asynchronous Procedure Calls (APC), or two-way messaging.
 * A message is treated as either a request or a response, where for every request there is
 * exactly one response.
 * </p>
 * <p>
 * Actors traditionally use one-way messaging, which can be difficult to understand when the code
 * of an actor becomes large. The apc package instead uses messages to implement something closer
 * to a method call. It also has the advantage of implicitly implementing flow control--which tends
 * to result in systems that behave well under load.
 * </p>
 * </p>
 * Another advantage of using two-way messages is improved exception handling. If an exception is
 * thrown while processing a request and there is no exception handler, then the exception is
 * passed up to the actor which sent the request, recursively.
 * </p>
 * <p>
 * An echo test running multiple pairs with 1000 messages per burst processed 49 million
 * messages per second--20 nanoseconds per message. But when messages are sent one at a
 * time, only 4255319 messages are processed per second--235 nanoseconds per message.
 * Sending messages in bursts, which simulates a system under load, is 1,075% faster.
 * </p>
 */

package org.agilewiki.jactor.apc;
