/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.interpro.scan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Location(s) of match on protein sequence
 *
 * @author  Antony Quinn
 * @version $Id: FingerPrintsLocation.java,v 1.4 2009/07/10 13:24:41 aquinn Exp $
 * @since   1.0
 */
@XmlType(name="FingerPrintsLocationType", propOrder={"start", "end"})
@Entity
public class FingerPrintsLocation
        extends AbstractLocation
        implements Location {

    @Column (nullable = false)
    private double pvalue;

    /**
     * protected no-arg constructor required by JPA - DO NOT USE DIRECTLY.
     */
    protected FingerPrintsLocation() {}

    public FingerPrintsLocation(int start, int end, double pvalue) {
        super(start, end);
        this.pvalue = pvalue;
    }

    @XmlAttribute(name="pvalue", required=true)
    public double getPValue() {
        return pvalue;
    }

    // TODO: Figure out which class to use (FingerPrintsMatch replaced by RawFingerPrintsMatch and FilteredFingerPrintsMatch)
    //@ManyToOne(targetEntity = FingerPrintsMatch.class)
    @XmlTransient
    @Override public Match getMatch() {
        return super.getMatch();
    }
}