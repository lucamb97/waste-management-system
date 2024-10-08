package wasteManagement.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BinService {
    @Autowired
    private final BinsRepository binsRepository;

    public List<Bin> getBinsByCity(String city) {
        return binsRepository.findByCity(city);
    }

    public Optional<Bin> getBinById(long id) {return binsRepository.findById(id);}

    public List<Bin> getBinByUser(String user) {return binsRepository.findByUser(user);}

    public void addBins(List<Bin> bins){binsRepository.saveAll(bins);}

    public void deleteBins(List<Long> ids) {binsRepository.deleteAllById(ids);}

    public void binFull(Long binId) {
        Optional<Bin> optionalBin = binsRepository.findById(binId);
        if(optionalBin.isPresent()){
            Bin bin = optionalBin.get();
            bin.setNeedsEmptying(true);
            binsRepository.save(bin);
        }
    }
}

