package uk.ac.ebi.interpro.scan.management.model.implementations.stepInstanceCreation.nucleotide;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import uk.ac.ebi.interpro.scan.management.model.StepInstance;
import uk.ac.ebi.interpro.scan.management.model.implementations.RunBinaryStep;
import uk.ac.ebi.interpro.scan.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Phil Jones
 *         Date: 21/06/11
 *         Time: 12:01
 */
public class RunTranslateToProteinsStep extends RunBinaryStep {

    private static final Logger LOGGER = Logger.getLogger(RunTranslateToProteinsStep.class.getName());

    private String fullPathToBinary;

    /**
     * The path / file name for the OUTPUT FILE (protein sequence fasta file).
     */
    private String fastaFilePath;

    /**
     * Minimum nucleotide size of ORF to report (Any integer value). Default value is 50.
     */
    private String minSize;

    /**
     * Maximum nucleotide size of ORF to report (Any integer value). Default value is 1000000.
     */
    private String maxSize;

    public static final String SEQUENCE_FILE_PATH_KEY = "nucleic.seq.file.path";

    public static final String MIN_NUCLEOTIDE_SIZE = "minsize";

    /**
     * Path to getorf binary.
     *
     * @param fullPathToBinary
     */
    @Required
    public void setFullPathToBinary(String fullPathToBinary) {
        this.fullPathToBinary = fullPathToBinary;
    }

    /**
     * Note this is the path template for the OUTPUT FILE - e.g. the protein sequence
     * file generated by GetOrf.
     *
     * @param fastaFilePath being the name of the protein sequence output file.
     */
    @Required
    public void setFastaFilePath(String fastaFilePath) {
        this.fastaFilePath = fastaFilePath;
    }

    @Required
    public void setMinSize(String minSize) {
        this.minSize = minSize;
    }

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected List<String> createCommand(StepInstance stepInstance, String temporaryFileDirectory) {
        final Map<String, String> parameters = stepInstance.getParameters();
        final String nucleicAcidSeqFilePath = parameters.get(SEQUENCE_FILE_PATH_KEY);
        final String minSizeCommandLine = parameters.get(MIN_NUCLEOTIDE_SIZE);
        if (minSizeCommandLine != null && minSizeCommandLine.length() > 0) {
            setMinSize(minSizeCommandLine);
        }
        final String fastaFile = stepInstance.buildFullyQualifiedFilePath(temporaryFileDirectory, fastaFilePath);
        final List<String> command = new ArrayList<String>();
        command.add(fullPathToBinary);
            /*
            Using the pearson format switch (undocumented feature!) means that getorf will
            use all text up to the first space as the identifier.
            This is essential for the short term nucletide header fix to work (IBU-2426)
            TODO - consider removing this switch when the long-term fix is implemented
             */
        command.add("-sf");
        command.add("pearson");
        command.add("-sequence");
        command.add(nucleicAcidSeqFilePath);
        command.add("-outseq");
        command.add(fastaFile);
        if (this.minSize != null) {
            command.add("-minsize");
            command.add(this.minSize);
        }
        if (this.maxSize != null) {
            command.add("-maxsize");
            command.add(this.maxSize);
        }
        // Need to build binary switches.
        // Need to have default minimum length (100?)
        command.addAll(getBinarySwitchesAsList());

        Utilities.verboseLog("RunGetOrfStep: " + getCommandBuilder(command));
        return command;
    }
}
