//package com.mobiledev.uom.flyme.classes;

/**
 * Created by Lefteris on 27/12/2016.
 */
/*
public class FlightAdapter extends ArrayAdapter {

        public List<FlightModel> flightModelList;
        private int resource;
        private LayoutInflater inflater;

        public FlightAdapter(Context context, int resource, List<FlightModel> objects){
            super(context, resource, objects);
            flightModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        //καλείται τόσες φόρες όσα και τα αντικείμενα που έχουμε
        @Override
        public View getView(int position, View convertView, ViewGroup parent){


            if (convertView == null){
                convertView = inflater.inflate(R.layout.list_item_flight, null);
            }

            TextView originLoc = (TextView)convertView.findViewById(R.id.originLoc);
            TextView destinationLoc = (TextView)convertView.findViewById(R.id.destinationLoc);
            TextView departDate = (TextView)convertView.findViewById(R.id.departureDate);
            TextView arrivalDate = (TextView)convertView.findViewById(R.id.arrivalDate);
            TextView currency = (TextView)convertView.findViewById(R.id.price_textView);
            TextView airline = (TextView)convertView.findViewById(R.id.airlineName);

            //καλώ το substring για να μην εμφανίζει το [code]
            originLoc.setText(originAirportName.substring(0,originAirportName.indexOf("[")-1));

            destinationLoc.setText(destinationAirportName.substring(0,destinationAirportName.indexOf("[")-1));

            departDate.setText(":" +" "+flightModelList.get(position).getDepartureDate());
            arrivalDate.setText(":" +" "+flightModelList.get(position).getArrivalDate());

            airline.setText(airlineMap.get(flightModelList.get(position).getAirline()));

            currency.setText(flightModelList.get(position).getPrice()
                    + " " + flightModelList.get(position).getCurrency());


            return convertView;
        }
    }*/