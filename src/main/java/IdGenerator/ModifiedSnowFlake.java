package IdGenerator;

import java.util.Random;

public class ModifiedSnowFlake {


    /**
     *  data of lunching
     */
    private final static long start_timestamp = 1684077470803L;
    /**
     * sequence part, machine/server part, datacenter part
     */
    private final static long sequence_bit = 12L;
    private final static long machine_bit = 5L;
    private final static long datacenter_bit = 5L;

    /**
     * maximum of each part
     */


    private final static long MAX_DATACENTER_NUM = ~(-1L << datacenter_bit);
    private final static long MAX_MACHINE_NUM = ~(-1L << machine_bit);
    private final static long MAX_SEQUENCE = ~(-1L << sequence_bit);

    /**
     *  shift of each part
     */
    private final static long machine_shift =  sequence_bit;

    private final static long datacenter_shift = sequence_bit + machine_bit;

    private final static long timestamp_shift = datacenter_shift + datacenter_bit;

    private final static long sequence_mask = ~(-1L << sequence_bit);

    private static final Random RANDOM = new Random();
    private long datacenterId;
    private long machineId;

    // 4096 id for high concurrency(request new id at the same time, issue max 4096 new id at this single millisecond)
    private long sequence = 0L;
    private long last_timestamp = -1L;



    /**
     * constructor with specified datacenter id and machine id
     * @param datacenterId datacenter id
     * @param machineId machine id
     */
    public ModifiedSnowFlake(long datacenterId, long machineId){

        if(datacenterId > MAX_DATACENTER_NUM || datacenterId < 0){
            throw new IllegalArgumentException("Invalid datacenter ID");
        }

        if( machineId > MAX_MACHINE_NUM || machineId < 0){
            throw new IllegalArgumentException("Invalid machine ID");
        }

        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }



    private synchronized long nextId(){
        // get curr time
        long timeStamp = getCurrTime();

        // clock move backwards
        if(timeStamp < last_timestamp){

            var diff = last_timestamp - timeStamp;
            // if it less than 5 millisecond, wait
            if(diff < 5){
                timeStamp = tillNextMillis(last_timestamp);
            }
            // change another machine/server,
            else{
                machineId = (machineId +1) & MAX_MACHINE_NUM;
                // if all machine are in used, change datacenter
                if(machineId == 0){
                    datacenterId = datacenterId + 1;
                }
            }
        }

        // request new id at the same time, sequence + 1
        if(timeStamp == last_timestamp){

            sequence = (sequence + 1) & sequence_mask;
            //if 4096 id are all sold out, just wait till next millis
            if(sequence == 0L){
                sequence = RANDOM.nextInt(100);
                timeStamp = tillNextMillis(last_timestamp);
            }
        }
        else{

            sequence = RANDOM.nextInt(100);
        }

        last_timestamp = timeStamp;


        return (timeStamp - start_timestamp) << timestamp_shift // timestamp part
                | datacenterId << datacenter_shift              // datacenter part
                | machineId << machine_shift                    // machine part
                | sequence;
    }

    /**
     *  convert long to string
     * @return string id
     */
    public String getString_nextId() {
        return Long.toString(nextId());
    }

    /**
     * wait until next millisecond
     * @param lastTimestamp last time stamp
     * @return time stamp
     */
    private long tillNextMillis(long lastTimestamp){
        long timestamp = getCurrTime();
        while(timestamp <= lastTimestamp){
            timestamp = getCurrTime();
        }
        return timestamp;
    }


    /**
     * get current time
     * @return current time
     */
    private long getCurrTime(){
        return System.currentTimeMillis();
    }

}
