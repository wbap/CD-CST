package cst;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Mind;

public class AgentMind extends Mind {
    public AgentMind() {
        super();

        Codelet cognitiveDistance = new CDCodelet();
        insertCodelet(cognitiveDistance);
        for(Codelet c: this.getCodeRack().getAllCodelets())
            c.setTimeStep(1);
        start();
    }
}