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
package org.agilewiki.jactor.apc;

import org.agilewiki.jactor.bufferedEvents.BufferedEventsDestination;
import org.agilewiki.jactor.bufferedEvents.BufferedEventsQueue;
import org.agilewiki.jactor.concurrent.ThreadManager;

abstract public class JAPCActor implements APCActor {

    private APCMailbox mailbox;

    /**
     * Handles callbacks from the inbox.
     */
    private RequestProcessor requestProcessor = new RequestProcessor() {
        private ExceptionHandler exceptionHandler;

        public ExceptionHandler getExceptionHandler() {
            return exceptionHandler;
        }

        public void setExceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public void haveEvents() {
            mailbox.dispatchEvents();
        }

        @Override
        public void processRequest(JAPCRequest request) throws Exception {
            JAPCActor.this.processRequest(request.getUnwrappedRequest(), new ResponseProcessor() {
                @Override
                public void process(Object unwrappedResponse) {
                    mailbox.response(unwrappedResponse);
                }
            });
        }
    };

    private RequestSource requestSource = new RequestSource() {
        @Override
        public void responseFrom(BufferedEventsQueue<APCMessage> eventQueue, JAPCResponse japcResponse) {
            eventQueue.send(mailbox, japcResponse);
        }

        @Override
        public void send(BufferedEventsDestination<APCMessage> destination, JAPCRequest japcRequest) {
            mailbox.send(destination, japcRequest);
        }
    };

    /**
     * Create a JAEventActor
     *
     * @param threadManager Provides a thread for processing dispatched events.
     */
    public JAPCActor(ThreadManager threadManager) {
        this(new JAPCMailbox(threadManager));
    }

    /**
     * Create a JAEventActor
     * Use this constructor when providing an implementation of BufferedEventsQueue
     * other than JABufferedEventsQueue.
     *
     * @param mailbox The actor's mailbox.
     */
    public JAPCActor(APCMailbox mailbox) {
        this.mailbox = mailbox;
        this.mailbox = mailbox;
    }

    /**
     * Set the initial capacity for buffered outgoing events.
     *
     * @param initialBufferCapacity The initial capacity for buffered outgoing events.
     */
    @Override
    final public void setInitialBufferCapacity(int initialBufferCapacity) {
        mailbox.setInitialBufferCapacity(initialBufferCapacity);
    }

    final protected ExceptionHandler getExceptionHandler() {
        return requestProcessor.getExceptionHandler();
    }

    final protected void setExceptionHandler(ExceptionHandler exceptionHandler) {
        requestProcessor.setExceptionHandler(exceptionHandler);
    }

    final protected void send(final APCActor actor, final Object data, final ResponseProcessor rd1) {
        ResponseProcessor rd2 = rd1;
        final ExceptionHandler exceptionHandler = requestProcessor.getExceptionHandler();
        if (exceptionHandler != null) rd2 = new ResponseProcessor() {
            @Override
            public void process(Object unwrappedResponse) throws Exception {
                requestProcessor.setExceptionHandler(exceptionHandler);
                rd1.process(unwrappedResponse);
            }
        };
        actor.acceptRequest(requestSource, data, rd2);
    }

    @Override
    final public void acceptRequest(RequestSource requestSource,
                                    Object data,
                                    ResponseProcessor rd) {
        JAPCRequest japcRequest = new JAPCRequest(requestSource, requestProcessor, data, rd);
        requestSource.send(mailbox, japcRequest);
    }

    final protected void iterate(final APCFunction apcFunction,
                                 final ResponseProcessor responseProcessor) throws Exception {
        ResponseProcessor rd = new ResponseProcessor() {
            @Override
            public void process(Object unwrappedResponse) throws Exception {
                if (unwrappedResponse == null)
                    apcFunction.process(this);
                else responseProcessor.process(unwrappedResponse);
            }
        };
        apcFunction.process(rd);
    }

    abstract protected void processRequest(Object data, ResponseProcessor responseProcessor)
            throws Exception;
}