package com.example.RiderService.services;



import com.example.RiderService.dto.RiderLocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    public void updateLocation(Long riderId, double lat, double lon){
        String key = "rider:" + riderId + ":location";

        Map<String , String> data= Map.of("lat",String.valueOf(lat),"lon",String.valueOf(lon),"updatedAt",String.valueOf(System.currentTimeMillis()));
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, TTL);
    }

    public Optional<RiderLocationResponse> getLocation(Long riderId){

        String key="rider:"+riderId+":location";
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data.isEmpty()) return Optional.empty();

        RiderLocationResponse res=new RiderLocationResponse();
        res.setRiderId(riderId);
        res.setLatitude(Double.parseDouble((String) data.get("lat")));
        res.setLongitude(Double.parseDouble((String) data.get("lon")));
        res.setUpdatedAt(Long.parseLong((String) data.get("updatedAt")));

        return Optional.of(res);




    }


}
