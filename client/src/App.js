import React, { Component } from 'react';
import './App.css';
import { Tab, Tabs } from "@material-ui/core/es/index";
import { Users } from "./Users";

class App extends Component {

  state = {
    value: 0,
  };

  handleChange = (event, value) => {
    this.setState({ value });
  };

  render() {
    return (
      <div className="App">
        <Tabs value={this.state.value} onChange={this.handleChange}>
          <Tab label={"Users"}/>
          <Tab label={"Items"}/>
          <Tab label={"Orders"}/>
        </Tabs>
        {this.state.value === 0 && <Users />}
      </div>
    );
  }
}

export default App;
