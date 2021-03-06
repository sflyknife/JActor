package org.agilewiki.jactor.pubsub.actorName;

import junit.framework.TestCase;
import org.agilewiki.jactor.JAMailboxFactory;
import org.agilewiki.jactor.MailboxFactory;

/**
 * Test code.
 */
public class ActorNameTest extends TestCase {
    public void test() {
        MailboxFactory mailboxFactory = JAMailboxFactory.newMailboxFactory(1);
        try {
            JActorName a = new JActorName(mailboxFactory.createMailbox());
            a.setActorName("foo");
            String nm = GetActorName.req.call(a);
            assertEquals("foo", nm);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mailboxFactory.close();
        }
    }
}
