package com.sdumagicode.backend.service;

import com.sdumagicode.backend.core.service.Service;
import com.sdumagicode.backend.dto.TransactionRecordDTO;
import com.sdumagicode.backend.entity.TransactionRecord;
import com.sdumagicode.backend.enumerate.TransactionEnum;

import java.util.List;

/**
 * @author ronger
 */
public interface TransactionRecordService extends Service<TransactionRecord> {
    /**
     * 交易
     *
     * @param transactionRecord
     * @return
     */
    TransactionRecord transfer(TransactionRecord transactionRecord);

    /**
     * 查询指定账户的交易记录
     *
     * @param bankAccount
     * @param startDate
     * @param endDate
     * @return
     */
    List<TransactionRecordDTO> findTransactionRecords(String bankAccount, String startDate, String endDate);

    /**
     * 根据用户主键进行交易
     *
     * @param toUserId
     * @param formUserId
     * @param transactionType
     * @return
     */
    TransactionRecord userTransfer(Long toUserId, Long formUserId, TransactionEnum transactionType);

    /**
     * 社区银行转账/奖励发放
     *
     * @param idUser
     * @param transactionType
     * @return
     */
    TransactionRecord bankTransfer(Long idUser, TransactionEnum transactionType);

    /**
     * 发放新手奖励
     *
     * @param transactionRecord
     * @return
     */
    TransactionRecord newbieRewards(TransactionRecord transactionRecord);
}
