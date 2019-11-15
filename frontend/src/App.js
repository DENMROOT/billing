import React, {Component} from 'react';
import Transactions from "./components/transactions";

class App extends Component {
    state = {
        transactions: []
    }

    componentDidMount() {
        fetch('http://localhost:8080/api/billing/v1/transactions/history')
            .then(res => res.json())
            .then((data) => {
                this.setState({transactions: data})
            })
            .catch(console.log)
    }

    render() {
        return (
            <div className="container">
                <div className="row">
                    <div className="col">
                        <Transactions transactions={this.state.transactions}/>
                    </div>
                </div>
            </div>


        );
    }
}

export default App;
