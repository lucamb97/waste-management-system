package wasteManagement.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wasteManagement.model.entitys.Bin;
import wasteManagement.model.repositorys.BinsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BinService {

    private final BinsRepository binsRepository;

    public List<Bin> getBinsByCity(String city) {
        return binsRepository.findByCity(city);
    }

    public Bin getBinById(long id) {
        return binsRepository.findById(id);
    }

    public void addBins(List<Bin> bins){
        binsRepository.saveAll(bins);
    }

    public void deleteBins(List<Long> ids) {binsRepository.deleteAllById(ids);}
}

