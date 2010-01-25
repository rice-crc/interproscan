package uk.ac.ebi.interpro.scan.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * Models a match based upon the Phobius algorithm against a
 * protein sequence.
 *
 * @author Phil Jones
 * @version $Id$
 * @since 1.0
 */
@Entity
@Table(name="phobius_match")
@XmlType(name="PhobiusMatchType")
public class PhobiusMatch extends Match<PhobiusMatch.PhobiusLocation> {

    protected PhobiusMatch() {}

    public PhobiusMatch(Signature signature, Set<PhobiusLocation> locations) {
        super(signature, locations);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhobiusMatch))
            return false;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(29, 49)
                .appendSuper(super.hashCode())
                .toHashCode();
    }
    /**
     * Location of Phobius match on a protein sequence
     *
     * @author Phil Jones
     */
    @Entity
    @Table(name="phobius_location")
    @XmlType(name="PhobiusLocationType")
    public static class PhobiusLocation extends Location {

        protected PhobiusLocation() {}

        public PhobiusLocation(int start, int end){
            super(start, end);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof PhobiusLocation))
                return false;
            return new EqualsBuilder()
                    .appendSuper(super.equals(o))
                    .isEquals();
        }

        @Override public int hashCode() {
            return new HashCodeBuilder(29, 53)
                    .appendSuper(super.hashCode())
                    .toHashCode();
        }
    }

}
