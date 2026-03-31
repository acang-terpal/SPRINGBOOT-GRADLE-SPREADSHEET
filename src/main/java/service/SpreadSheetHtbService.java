package service;

import entity.EntityHtb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.SpreadSheetHtbRepo;

import java.util.HashMap;
import java.util.List;

@Service
public class SpreadSheetHtbService {

    SpreadSheetHtbRepo SpreadSheetHtbRepo;

    @Autowired
    public SpreadSheetHtbService(SpreadSheetHtbRepo SpreadSheetHtbRepo) {
        this.SpreadSheetHtbRepo = SpreadSheetHtbRepo;
    }

    public List<EntityHtb> findAllParentRootPaginated(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<EntityHtb> htbEntities = SpreadSheetHtbRepo.findAllParentRootPaginated(pageable);
        return htbEntities.getContent();
    }

    public List<EntityHtb> findAllChildrenPaginated(HashMap<String, Object> object, int page) {
        List<EntityHtb> htbEntities = SpreadSheetHtbRepo.findAllChildrenPaginated(
                object.get("class_id").toString(),
                1000,
                page - 1);
        return htbEntities;
    }

    public List<EntityHtb> findHtbTree() {
        return SpreadSheetHtbRepo.findHtbTree();
    }
}
