import React from 'react'

const Transactions = ({transactions: transactions}) => {
    return (
        <div>
            <table className="table table-bordered">
                <thead className="thead-dark">
                <tr>
                    <th scope="col">TransactionId</th>
                    <th scope="col">AccountId</th>
                    <th scope="col">Type</th>
                    <th scope="col">Amount</th>
                    <th scope="col">EffectiveDate</th>
                </tr>
                </thead>

                {transactions.map((transaction) => (
                    <tbody>
                    <tr className={transaction.amount >= 0 ? "table-success" : "table-danger"}>
                        <td class="text-center">{transaction.transactionId}</td>
                        <td class="text-center">{transaction.accountId}</td>
                        <td class="text-center">{transaction.type}</td>
                        <td class="text-center">{transaction.amount}</td>
                        <td class="text-center">{new Date(transaction.effectiveDate).toISOString()}</td>
                    </tr>
                    </tbody>
                ))}
            </table>
        </div>
    )
};

export default Transactions
