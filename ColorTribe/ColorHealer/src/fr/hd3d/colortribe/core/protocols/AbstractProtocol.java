package fr.hd3d.colortribe.core.protocols;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

abstract public class AbstractProtocol implements ICalibProtocol
{

    protected final EventListenerList listeners = new EventListenerList();
    

    public interface ProtocolListener extends EventListener {
        public void newMeasure(ProtocolEvent event);
        public void measuresAbortion(ProtocolEvent event);
        public void setStepAsked(ProtocolEvent event);
    }
    
    public class ProtocolEvent extends EventObject {
        /**
         * 
         */
        private static final long serialVersionUID = -6263767830133559172L;
        public ProtocolEvent(Object source) {
            super(source);
        }
    }
    
    public void addProtocolListener(ProtocolListener listener) {
        listeners.add(ProtocolListener.class, listener);
    }

    public void removeProtocolListener(ProtocolListener l) {
        listeners.remove(ProtocolListener.class, l);
    }

    public boolean isListenerAlreadyRegistered(ProtocolListener listener) {
        for (int i = 0; i < listeners.getListenerCount(ProtocolListener.class); i++) {
            ProtocolListener crntListener = listeners.getListeners(ProtocolListener.class)[i];
            if (crntListener == listener)
                return true;
        }
        return false;
    }

    public void notifyMeasuresSetChanged() {
        ProtocolListener[] listenerList = listeners.getListeners(ProtocolListener.class);
        for (ProtocolListener listener : listenerList)
            listener.newMeasure(new ProtocolEvent(this));
    }
    public void notifyMeasuresAborted() {
        ProtocolListener[] listenerList = listeners.getListeners(ProtocolListener.class);
        for (ProtocolListener listener : listenerList)
            listener.measuresAbortion(new ProtocolEvent(this));
    }
    public void notifySetStepAsked() {
        ProtocolListener[] listenerList = listeners.getListeners(ProtocolListener.class);
        for (ProtocolListener listener : listenerList)
            listener.setStepAsked(new ProtocolEvent(this));
    }
}
