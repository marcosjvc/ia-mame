package org.tibennetwork.iamame.mame;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaDevice {

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Instance {
        
        @XmlAttribute(name="briefname")
        private String briefname;

        public String getBriefname() {
            return briefname;
        }
    }

    @XmlAttribute(name="type")
    private String type;

    @XmlAttribute(name="interface")
    private String mediaInterface;

    @XmlElement(name="instance")
    Instance instance;

    public String getBriefname() {
        return instance.getBriefname();
    }

    public String getMediaInterface() {
        return mediaInterface;
    }

    public String toString () {
        return String.format(
            "Device: [type: %s, interface: %s, briefname: %s]",
            this.type, 
            this.mediaInterface,
            this.getBriefname());
    }

}
