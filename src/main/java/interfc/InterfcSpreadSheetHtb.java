package interfc;

import java.util.HashMap;

/**
 *
 * @author hasan
 */
public interface InterfcSpreadSheetHtb {

    HashMap getRootRateDownload(HashMap objRoot);

    HashMap getChildrenRateDownload(HashMap objRoot);

    HashMap getRootRateUpload(HashMap objRoot);

    HashMap getChildrenRateUpload(HashMap objDataTier1);

    HashMap getRootTotalDownload(HashMap objRoot);

    HashMap getChildrenTotalDownload(HashMap objRoot);

    HashMap getRootTotalUpload(HashMap objRoot);

    HashMap getChildrenTotalUpload(HashMap objDataTier1);
}
