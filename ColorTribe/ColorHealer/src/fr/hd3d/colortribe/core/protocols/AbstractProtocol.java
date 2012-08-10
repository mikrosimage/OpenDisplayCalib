package fr.hd3d.colortribe.core.protocols;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

abstract public class AbstractProtocol implements ICalibProtocol
{

    private final EventListenerList listeners = new EventListenerList();
    

    public interface ProtocolListener extends EventListener {
        public void newMeasure(ProtocolEvent event);
        
        public void setStepAsked(ProtocolEvent event);
    }
    
    public class ProtocolEvent extends EventObject {
        /**
         * 
         */
        private static final long serialVersionUID = -6263767830133559172L;
        private ProtocolEvent(Object source) {
            super(source);
        }
    }
    
    public void addProtocolListener(ProtocolListener listener) {
        listeners.add(ProtocolListener.class, listener);
    }

    

    

    void notifyMeasuresSetChanged() {
        ProtocolListener[] listenerList = listeners.getListeners(ProtocolListener.class);
        for (ProtocolListener listener : listenerList)
            listener.newMeasure(new ProtocolEvent(this));
    }
    
    public void notifySetStepAsked() {
        ProtocolListener[] listenerList = listeners.getListeners(ProtocolListener.class);
        for (ProtocolListener listener : listenerList)
            listener.setStepAsked(new ProtocolEvent(this));
    }
}
