package org.agilewiki.jactor.basics;

import junit.framework.TestCase;
import org.agilewiki.jactor.JAFuture;
import org.agilewiki.jactor.JAMailboxFactory;
import org.agilewiki.jactor.Mailbox;
import org.agilewiki.jactor.MailboxFactory;

/**
 * Test code.
 */
public class Test7 extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = JAMailboxFactory.newMailboxFactory(10);
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor7a actor7a = new Actor7a(mailbox);
        JAFuture future = new JAFuture();
        boolean response = Parallel.req.send(future, actor7a);
        assertEquals(true, response);
        mailboxFactory.close();
    }
}
