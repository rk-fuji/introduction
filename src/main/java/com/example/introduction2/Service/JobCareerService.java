package com.example.introduction2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.dto.JobCareerRequest;
import com.example.introduction2.entity.JobCareer;
import com.example.introduction2.entity.User;
import com.example.introduction2.repository.JobCareerRepository;
import com.example.introduction2.util.DateUtil;

@Service
public class JobCareerService {
    @Autowired
    private JobCareerRepository jobCareerRepository;

    public JobCareer getJobCareer(Integer id) {
        return jobCareerRepository.findById(id).orElseThrow();
    }

    public void createJobCareer(User user) {
        var jobCareer = new JobCareer();
        jobCareer.setUser(user);
        jobCareerRepository.save(jobCareer);
    }

    public JobCareer update(User user, JobCareerRequest jobCareerRequest) {
        var jobCareer = user.getJobCareer();
        jobCareer.setNameKana(jobCareerRequest.getNameKana());
        jobCareer.setAffiliation(jobCareerRequest.getAffiliation());
        jobCareer.setNearestStation(jobCareerRequest.getNearestStation());
        jobCareer.setSpouse(Integer.parseInt(jobCareerRequest.getSpouse()));
        jobCareer.setOperation(DateUtil.StringDateToLocalDate(jobCareerRequest.getOperation()));
        jobCareer.setCareerDate(jobCareerRequest.getCareerDate());
        jobCareer.setCareerMonth(jobCareerRequest.getCareerMonth());
        jobCareer.setSpecialty(jobCareerRequest.getSpecialty());
        jobCareer.setFavoriteTechnology(jobCareerRequest.getFavoriteTechnology());
        jobCareer.setFavoriteBusiness(jobCareerRequest.getFavoriteBusiness());
        jobCareer.setSelfPublicRelations(jobCareerRequest.getSelfPublicRelations());
        jobCareerRepository.save(jobCareer);
        return jobCareer;
    }
}
