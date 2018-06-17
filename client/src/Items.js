import React, {Component} from "react";
import axios from "axios";
import {Button, TextField} from "@material-ui/core/es/index";

export class Items extends Component {

    constructor(props) {
        super(props);
        this.fetchItems();
        this.state = {
            items: [],
            name: "",
            price: 0,
        };
    }

    componentWillReceiveProps = (props) => {
        this.fetchItems();
    };

    fetchItems = () => {
        fetch("http://localhost:8002/item/all/")
            .then(response => response.json())
            .then(items => this.setState({items}));
    };

    // getPrettyPrint = (event) => {
    //     const str = JSON.stringify(event, null, 2);
    //     return str.slice(1, str.length - 1);
    // };
    //
    // getEvents = () => {
    //     return this.state.events.filter(event => {
    //         const str = this.getPrettyPrint(event);
    //         return str.includes(this.state.filter);
    //     }).sort((a, b) => b.createdAt.localeCompare(a.createdAt));
    // };

    addItem = () => {
        const {name} = this.state;
        const price = Number(this.state.price);
        if(!name || !name.trim()) {
            return;
        }
        if(!price || typeof price !== "number") {
            return;
        }
        console.log(name, price);
        return axios.post("http://localhost:8002/item/add", {name, price})
            .then(({data: item}) => {
                const items = [...this.state.items];
                items.push(item);
                this.setState({items});
            });
    }


    render() {
        const {items} = this.state;
        return (
            <div>
                <div style={{display: "flex", flexDirection: "row", width: "100%", justifyContent: "center"}}>
                    <TextField id={"name"}
                               label={"name"}
                               value={this.state.name}
                               onChange={(e) => this.setState({name: e.target.value})}
                               style={{margin: "4px"}}
                    />
                    <TextField id={"price"}
                               label={"price"}
                               value={this.state.price}
                               onChange={(e) => this.setState({price: e.target.value})}
                               style={{margin: "4px"}}
                    />
                    <Button color="primary" style={{margin: "4px"}} onClick={this.addItem}>
                        Add
                    </Button>
                </div>
                <hr/>
                {items.map(item => {
                    return (
                        <div key={item.id} style={{display: "flex", flexDirection: "row", justifyContent: "center"}}>
                            <div style={{margin: "4px"}}>{item.name}</div>
                            <div>${item.price}</div>
                        </div>
                    )
                })}
            </div>
        )
    }
}