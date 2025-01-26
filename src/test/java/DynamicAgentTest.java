import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.net.URL;

public class DynamicAgentTest {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException {
        // your pid of remote jvm application
        String pid = "2841";
        URL agentUrl = DynamicAgentTest.class.getClassLoader()
                .getResource("MetricsAgent-1.0-SNAPSHOT.jar");
        VirtualMachine vm = VirtualMachine.attach(pid);
        try {
            vm.loadAgent( agentUrl.getPath(),"org.va");
        } catch (AgentInitializationException e) {
            System.err.println("Agent initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
        vm.detach();

    }
}
