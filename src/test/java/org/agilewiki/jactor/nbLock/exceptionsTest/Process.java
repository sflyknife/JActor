package org.agilewiki.jactor.nbLock.exceptionsTest;

import org.agilewiki.jactor.ExceptionHandler;
import org.agilewiki.jactor.Mailbox;
import org.agilewiki.jactor.RP;
import org.agilewiki.jactor.actorName.ActorName;
import org.agilewiki.jactor.actorName.GetActorName;
import org.agilewiki.jactor.actorName.JActorName;
import org.agilewiki.jactor.nbLock.Lock;
import org.agilewiki.jactor.nbLock.Unlock;

/**
 * Test code.
 */
public class Process
        extends JActorName
        implements Does {
    public Process(Mailbox mailbox) {
        super(mailbox);
    }

    @Override
    protected void processRequest(Object request, final RP rp) throws Exception {
        Class reqcls = request.getClass();

        if (reqcls == DoItEx.class) {
            final String me = GetActorName.req.call((ActorName) this);
            Lock.req.send(this, this, new RP<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    setExceptionHandler(new ExceptionHandler() {
                        @Override
                        public void process(Exception exception) throws Exception {
                            System.out.println(me + " got exception " + exception);
                            Unlock.req.send(Process.this, Process.this, rp);
                        }
                    });
                    System.out.println("start " + me);
                    Thread.sleep(100);
                    throw new Exception("from " + me);
                }
            });
            return;
        }

        super.processRequest(request, rp);
    }
}
