package com.example.introduction2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.dto.CareerRequest;
import com.example.introduction2.entity.Career;
import com.example.introduction2.entity.JobCareer;
import com.example.introduction2.repository.CareerRepository;
import com.example.introduction2.util.DateUtil;

@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    public Career getCareer(Integer id) {
        return careerRepository.findById(id).orElseThrow();
    }

    public Career createCareer(CareerRequest careerRequest, JobCareer jobCareer) {
        var career = new Career();
        career.setFromDate(DateUtil.StringDateToLocalDate(careerRequest.getFromDate()));
        career.setToDate(DateUtil.StringDateToLocalDate(careerRequest.getToDate()));
        career.setBusinessOutline(careerRequest.getBusinessOutline());
        career.setBusinessOutlineDescription(careerRequest.getBusinessOutlineDescription());
        career.setRole(careerRequest.getRole());
        career.setRoleDescription(careerRequest.getRoleDescription());
        career.setUseLanguage(careerRequest.getUseLanguage());
        career.setUseDatabase(careerRequest.getUseDatabase());
        career.setUseServer(careerRequest.getUseServer());
        career.setOther(careerRequest.getOther());
        career.setResponsibleProcess(careerRequest.getResponsibleProcess());
        career.setJobCareer(jobCareer);

        careerRepository.save(career);
        return career;
    }

    public Career updateCareer(Integer careerId, CareerRequest careerRequest, JobCareer jobCareer) {
        var career = getCareer(careerId);
        career.setFromDate(DateUtil.StringDateToLocalDate(careerRequest.getFromDate()));
        career.setToDate(DateUtil.StringDateToLocalDate(careerRequest.getToDate()));
        career.setBusinessOutline(careerRequest.getBusinessOutline());
        career.setBusinessOutlineDescription(careerRequest.getBusinessOutlineDescription());
        career.setRole(careerRequest.getRole());
        career.setRoleDescription(careerRequest.getRoleDescription());
        career.setUseLanguage(careerRequest.getUseLanguage());
        career.setUseDatabase(careerRequest.getUseDatabase());
        career.setUseServer(careerRequest.getUseServer());
        career.setOther(careerRequest.getOther());
        career.setResponsibleProcess(careerRequest.getResponsibleProcess());
        career.setJobCareer(jobCareer);

        careerRepository.save(career);
        return career;
    }

    public void deleteCareer(Integer id) {
        careerRepository.deleteById(id);
    }

    public void showRequestLog(CareerRequest req) {
        System.out.println("==================== CareerRequest Parameter start");
        System.out.println(String.format("fromDate: [%s]", req.getFromDate()));
        System.out.println(String.format("toDate: [%s]", req.getToDate()));
        System.out.println(String.format("businessOutline: [%s]", req.getBusinessOutline()));
        System.out.println(String.format("businessOutlineDescription: [%s]", req.getBusinessOutlineDescription()));
        System.out.println(String.format("role: [%s]", req.getRole()));
        System.out.println(String.format("roleDescription: [%s]", req.getRoleDescription()));
        System.out.println(String.format("useLanguage: [%s]", req.getUseLanguage()));
        System.out.println(String.format("useDatabase: [%s]", req.getUseDatabase()));
        System.out.println(String.format("useServer: [%s]", req.getUseServer()));
        System.out.println(String.format("other: [%s]", req.getOther()));
        System.out.println(String.format("responsibleProcess: [%s]", req.getResponsibleProcess()));
        System.out.println("==================== CareerRequest Parameter end");
    }

}
